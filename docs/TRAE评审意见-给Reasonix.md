# TRAE 改动评审意见 · 给主开发 Reasonix

> **日期**：2026-07-01
> **撰写**：TRAE
> **接收**：Reasonix（DeepSeek v4 Pro），主开发
> **目的**：TRAE 于 6/30–7/1 对三端做了代码审查 + 安全红线修复，期间引入了一次回归（已修）。请 Reasonix 审查全部改动的正确性与完整性，文末附有需要你重点关注的疑点清单。

---

## 一、改动总览

TRAE 共改动 **14 个文件**（含 3 个新增文件），涉及后端、admin-web、miniapp 三端。完整问题清单见 `property-system-audit/property-system-audit.html`（65 项，本次仅修了阶段一的 8 项高危 + 中危）。

| # | 文件 | 改动类型 | 对应问题编号 |
|---|---|---|---|
| 1 | `backend/dto/WechatLoginRequest.java` | 重构 | H2 |
| 2 | `backend/config/WechatProperties.java` | 新增 | H2 |
| 3 | `backend/security/WechatUtil.java` | 新增 | H2 |
| 4 | `backend/controller/AuthController.java` | 重构 | H2 |
| 5 | `backend/controller/VisitorController.java` | 修复 | H1 |
| 6 | `backend/security/SecurityConfig.java` | 修复 | 回归修复 |
| 7 | `backend/resources/application.yml` | 修复 | H6 |
| 8 | `admin-web/src/views/Login.vue` | 修复 | A3 |
| 9 | `admin-web/src/utils/request.js` | 修复 | A4 |
| 10 | `miniapp/pages/login.vue` | 重构 | H2 |
| 11 | `miniapp/pages/owner/index.vue` | 修复 | P1 |
| 12 | `miniapp/pages/owner/repair.vue` | 修复 | P2 |
| 13 | `miniapp/pages/employee/index.vue` | 修复 | P2 |
| 14 | `miniapp/pages/government/index.vue` | 修复 | P2 |

---

## 二、逐项改动说明与待审点

### 2.1 微信登录 code2session 重构（H2）—— 最大改动，请重点审

**改了什么**：
- `WechatLoginRequest`：移除 `phone` 字段，改为 `code`（正式链路）+ `demoKey`（开发模式）
- 新增 `WechatProperties`：`wechat.appid` / `wechat.secret` 走环境变量 `${WECHAT_APPID}` / `${WECHAT_SECRET}`
- 新增 `WechatUtil`：封装 `code2session` 调用（hutool HttpUtil），用 code 换 openid
- `AuthController.wechatLogin`：重构为 code → openid → 匹配/建号 → 发 token
- miniapp `login.vue`：改为 `uni.login()` 拿 code 再请求，Demo 按钮传 demoKey

**设计意图**：用户确认微信授权登录不需要短信验证码，身份由微信担保。原代码裸传手机号是漏洞，改为走标准 code2session 链路。

**⚠️ 待 Reasonix 审查的疑点**：

1. **正式链路下的「首次登录绑定」流程缺失**：当前正式链路（配置了 AppID）下，如果 openid 未匹配到本地账号，直接返回"该微信号未绑定系统账号"。但原来用手机号匹配的「自动建号」逻辑（owner/employee/government 三表 phone 匹配）被我移除了。**这意味着正式上线后，已登记的业主第一次用微信登录会失败**——因为他们的 sys_user.username 还是手机号，不是 openid。
   - 我的考虑：安全优先，不能裸传手机号。但这确实破坏了原有的「首次登录自动建号」体验。
   - **需要你决定**：正式链路下首次登录如何绑定？方案 A：管理员后台手动绑定 openid；方案 B：首次登录时让用户输入手机号 + 短信验证码完成绑定（这才有验证码的位置）；方案 C：其他方案。

2. **Demo 模式的角色是写死的 OWNER**：`createDemoUser` 里 `user.setRoleType(Constants.ROLE_OWNER)`。如果开发时想测员工/政府端，Demo 模式进不去。原来输入手机号能匹配到员工/政府。
   - **需要你决定**：Demo 模式是否需要支持选择角色？

3. **`AuthController.createUser` 变成死代码**：原来被 wechatLogin 调用，现在不调了。我没删（怕影响其他地方），但它确实是 private 的，编译器没报错只是警告。建议清理或保留备用。

4. **`WechatUtil` 用了 hutool 的 `HttpUtil.get`**：项目已有 hutool 依赖（pom.xml 确认），但如果你们对 HTTP 客户端有统一规范（比如用 RestTemplate / WebClient），需要统一。

### 2.2 访客登记写路径 IDOR（H1）

**改了什么**：`VisitorController.register` 加 `@PreAuthorize("hasRole('OWNER')")` + `@CurrentUser SysUser user`，强制 `visitor.setOwnerId(user.getOwnerId())` + `setId(null)`。

**待审点**：
- 加了 `hasRole('OWNER')` 后，**员工/管理员不能再代业主登记访客**。原来没有角色限制。如果业务上需要员工代登记，需改为 `hasAnyRole('OWNER','EMPLOYEE','ADMIN')` 并在 EMPLOYEE/ADMIN 分支用前端传参。
- 请确认 `@CurrentUser` 在所有登录方式下都能正确解析（JwtAuthenticationFilter 设置的 authentication 里 principal 是 SysUser）。

### 2.3 admin-web request.js 401/403 拆分（A4）+ SecurityConfig 回归修复

**改了什么**：
- `request.js`：401 → 清 token + 跳登录；403 → 仅提示「无操作权限」，不清 token 不跳转
- `SecurityConfig`：补 `authenticationEntryPoint`（返回 401）+ `accessDeniedHandler`（返回 403）

**⚠️ 这是我引入回归又修复的地方，请重点审**：
- **回归经过**：我先改了前端 request.js（6/30），但没改后端 SecurityConfig。后端原来靠 Spring Security 默认行为把所有未认证请求返回 403。改之前 403 会跳登录（歪打正着），改之后 403 不跳了 → 用户卡在操作台。7/1 用户测试发现，我才补了 SecurityConfig。
- **教训**：改前端错误处理前，应该先验证后端实际返回的 HTTP 状态码。
- **请验证**：SecurityConfig 的 entryPoint / accessDeniedHandler 返回的 JSON 格式是否与 `Result` 类的序列化格式一致（我手写了 `{"code":401,"msg":"...","data":null}`，如果 `Result` 有额外字段或不同序列化方式，前端解析可能不一致）。

### 2.4 application.yml 密钥走环境变量（H6）

**改了什么**：`password: ***`（原为明文密码，已脱敏）→ `${DB_PASSWORD:***}`，`username: root` → `${DB_USERNAME:root}`，JWT secret → `${JWT_SECRET:...}`。

**待审点**：
- 开发环境保留了密码默认值（此处已脱敏），若生产环境不设 `DB_PASSWORD` 环境变量，会回退到该默认值——不安全。**建议生产环境不设默认值**（即 `${DB_PASSWORD}` 不带冒号默认值），强制必须配置。但这会影响开发便利性，需要你权衡。
  （9/28 后记：本条已落地——默认值彻底移除，本地开发改走不入库的 `application-local.yml`，Git 历史已清洗。）

### 2.5 admin-web Login.vue 硬编码凭据（A3）

**改了什么**：`reactive({ username: 'admin', password: '123456' })` → `import.meta.env.DEV ? 'admin' : ''`。

**待审点**：
- 构建产物（`dist/`）里是否真的不含默认凭据？我没有验证打包后的 JS 内容。建议 Reasonix 在 `dist/assets/Login-*.js` 里 grep 一下 `123456` 确认。

### 2.6 miniapp 修复（P1 / P2）

**P1（首页公告取值）**：`this.notices = await getNotices(null, 1) || []` → `const res = ...; this.notices = (res && res.list) || []`。改动简单，风险低。

**P2（上传 JSON.parse 容错）**：三处 `JSON.parse(r.data)` 包了 try/catch。改动简单，风险低。

**待审点**：
- P1 修复后 `viewNotice(id)` 传的是 `noticeId`，但 `notices.vue` 的 `onLoad` 没读这个参数（这是审查报告里的 P5，本次未修）。首页公告点击跳转后仍无法定位详情，但至少列表能正常显示了。
- miniapp 没有构建工具可用，我无法实际编译验证，只做了语法核对。建议 Reasonix 在 HBuilderX 里编译一次确认。

---

## 三、TRAE 自我检讨

| 问题 | 原因 | 教训 |
|---|---|---|
| request.js 403 拆分引入回归 | 改前端前没验证后端实际返回的状态码 | 前后端联动的改动必须一起改，或改前先打接口验证 |
| 正式链路首次登录绑定流程缺失 | 只顾着堵漏洞，没考虑原有「自动建号」业务流程被破坏 | 安全修复不能只堵不疏，需同步考虑合法用户的使用路径 |
| Demo 模式角色写死 OWNER | 图省事，没考虑开发时测员工/政府端的需求 | Demo 模式应尽量保留原有测试能力 |
| SecurityConfig 回归是用户发现而非自测发现 | 编译通过 ≠ 功能正确，我没有运行时验证条件 | 应在改动说明里标注「未做运行时验证，请主开发实测」 |

---

## 四、需要 Reasonix 重点确认的 5 件事

1. **微信登录正式链路首次绑定方案**（2.1 疑点 1）：正式上线后已登记业主首次微信登录如何绑定 openid？这是上线前必须解决的问题。
2. **SecurityConfig 返回的 JSON 格式与 Result 类是否一致**（2.3）：手写 JSON 可能与 Jackson 序列化有差异。
3. **VisitorController.register 加 hasRole('OWNER') 是否影响员工代登记**（2.2）：需确认业务需求。
4. **application.yml 生产环境是否应去掉默认值**（2.4）：默认值存在时生产环境可能回退到弱密码的风险。（✅ 9/28 已处理：默认值移除，密码改走不入库的本地私有配置）
5. **miniapp 改动在 HBuilderX 中实际编译**（2.6）：我无法编译验证，需你确认。

---

## 五、未修复的遗留问题（本次不在范围内，见审查报告）

以下高危/中危问题本次未修，仍在待办中（见《主开发文档》第 8 节）：

- 🔴 H3/H4：报修详情/评价越权
- 🔴 H5：缴费金额无校验（可负数/超额）
- 🟠 M2：管家消息 ownerId 误当 roomId
- 🟠 M3：工单流水分页错误（双括号初始化 + total 取错）
- 🟠 M10：状态码语义不一致
- 🟠 A1：admin 弹窗新增/编辑状态串档（6 页）
- 🟠 A2/P6：WorkOrderFeed N+1（三端共性）

完整清单见 `property-system-audit/property-system-audit.html`。

---

> 以上。请 Reasonix 审查后，在《主开发文档》第 9 节同步审查结论。如有疑问可直接在本文档下方追加批注。
