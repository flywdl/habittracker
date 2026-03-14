# GitHub Actions构建指南

## 🚀 使用GitHub Actions构建APK

### 为什么使用GitHub Actions？

✅ **免费** - GitHub提供免费的构建时间
✅ **快速** - 5-10分钟完成构建
✅ **简单** - 只需上传代码，点击运行
✅ **可靠** - GitHub服务器稳定快速
✅ **自动** - 可以配置自动构建
✅ **任何设备** - 不需要本地环境

---

## 📋 步骤1：创建GitHub仓库（如果没有）

### 1.1 注册GitHub账号

1. 访问 https://github.com
2. 点击 "Sign up"
3. 填写用户名、邮箱、密码
4. 验证邮箱

### 1.2 创建新仓库

1. 登录GitHub
2. 点击右上角 "+" → "New repository"
3. 填写信息：
   - Repository name: `habittracker`
   - Description: `习惯打卡应用`
   - 选择 Public 或 Private（推荐Public）
4. 勾选 "Initialize this repository with a README"（可选）
5. 点击 "Create repository"

---

## 📋 步骤2：上传项目代码

### 2.1 方法1：通过网页上传（最简单）

**适用情况：**
- 项目文件不多
- 不熟悉Git命令
- 想要最快方式

**步骤：**

1. 打开刚创建的GitHub仓库
2. 点击 "Upload files"
3. 拖拽以下文件到网页：
   ```
   整个 HabitTracker 文件夹
   ```
4. 在 "Commit changes" 输入：
   - Commit message: `Initial commit`
5. 点击 "Commit changes"

**注意：**
- 不要只上传gradle文件，要上传所有文件
- 确保上传了 `app/` 目录下的所有文件
- 确保上传了 `.github/workflows/` 目录

### 2.2 方法2：使用Git命令行（推荐）

**适用情况：**
- 熟悉Git命令
- 项目文件较多
- 需要版本控制

**步骤：**

```bash
# 进入项目目录
cd /workspace/projects/workspace/HabitTracker

# 初始化Git仓库
git init

# 添加所有文件
git add .

# 提交
git commit -m "Initial commit - Habit Tracker App"

# 添加远程仓库
git remote add origin https://github.com/你的用户名/habittracker.git

# 推送到GitHub
git branch -M main
git push -u origin main
```

**如果遇到认证问题：**
```bash
# 使用Personal Access Token
# 1. 访问 https://github.com/settings/tokens
# 2. 生成新的token（勾选repo权限）
# 3. 使用token代替密码
```

---

## 📋 步骤3：运行GitHub Actions

### 3.1 手动触发构建

1. 打开GitHub仓库
2. 点击 "Actions" 标签
3. 选择 "Build Android APK" 工作流
4. 点击 "Run workflow"
5. 选择分支（通常是 `main`）
6. 选择构建类型：
   - `debug` - 测试版本
   - `release` - 发布版本（需要签名）
7. 点击 "Run workflow" 按钮

### 3.2 自动触发构建（可选）

如果你配置了自动触发，每次推送代码到 `main` 分支会自动构建。

**如何配置自动触发：**
- 工作流文件已配置好 `push` 事件
- 每次推送代码会自动运行
- 可以在Settings中禁用

---

## 📋 步骤4：下载APK

### 4.1 查看构建状态

1. 等待构建完成（5-10分钟）
2. 在 "Actions" 标签查看运行状态
3. 绿色 ✅ = 成功
4. 红色 ❌ = 失败（点击查看日志）

### 4.2 下载APK

1. 点击成功的构建任务
2. 向下滚动到 "Artifacts" 部分
3. 点击下载：
   - `habit-tracker-debug` - Debug版本
   - `habit-tracker-release` - Release版本
4. 解压下载的zip文件
5. 找到 `.apk` 文件

**APK文件位置：**
- Debug: `app/build/outputs/apk/debug/app-debug.apk`
- Release: `app/build/outputs/apk/release/app-release.apk`

---

## 🔍 步骤5：查看构建日志

如果构建失败，查看日志找出问题：

1. 点击失败的构建任务
2. 点击 "Build Android APK" 展开
3. 查看每个步骤的日志
4. 找到错误信息

**常见错误：**

1. **依赖下载失败**
   - 现象：Could not resolve dependencies
   - 解决：重试构建（GitHub网络问题）

2. **编译错误**
   - 现象：Compilation failed
   - 解决：检查代码语法，修复后重新提交

3. **签名错误（Release构建）**
   - 现象：Signing failed
   - 解决：配置keystore或只构建Debug版本

---

## 📱 步骤6：安装和测试

### 6.1 安装到Android设备

**方法1：通过ADB安装**
```bash
# 连接设备
adb devices

# 安装APK
adb install app-debug.apk

# 如果已安装，覆盖安装
adb install -r app-debug.apk
```

**方法2：直接安装**
1. 将APK文件复制到手机
2. 在手机上点击APK文件
3. 允许安装未知来源应用
4. 点击安装

### 6.2 测试应用功能

**测试清单：**
- [ ] 打开应用
- [ ] 添加习惯
- [ ] 打卡
- [ ] 查看统计
- [ ] 删除习惯
- [ ] 设置提醒
- [ ] 关闭应用再打开（数据持久化）

---

## ⚙️ 高级配置

### 配置自动构建

每次推送代码自动构建：

```yaml
# .github/workflows/build-apk.yml
on:
  push:
    branches: [ main, master ]
  workflow_dispatch:
    # 手动触发
```

### 配置多平台构建

同时构建多个架构：

```yaml
strategy:
  matrix:
    abi: ['armeabi-v7a', 'arm64-v8a', 'x86', 'x86_64']

steps:
  - uses: actions/checkout@v4
  - name: Build for ${{ matrix.abi }}
    run: ./gradlew assembleDebug --abi ${{ matrix.abi }}
```

### 配置签名（Release构建）

在GitHub Secrets中配置keystore：

1. 仓库 → Settings → Secrets and variables → Actions
2. 添加以下Secrets：
   - `KEYSTORE_FILE`: Base64编码的keystore文件
   - `KEYSTORE_PASSWORD`: keystore密码
   - `KEY_ALIAS`: key别名
   - `KEY_PASSWORD`: key密码

3. 在工作流中使用：

```yaml
- name: Decode keystore
  run: echo "${{ secrets.KEYSTORE_FILE }}" | base64 -d > keystore.jks

- name: Build Release APK
  run: ./gradlew assembleRelease
  env:
    KEYSTORE_PASSWORD: ${{ secrets.KEYSTORE_PASSWORD }}
    KEY_ALIAS: ${{ secrets.KEY_ALIAS }}
    KEY_PASSWORD: ${{ secrets.KEY_PASSWORD }}
```

---

## 📊 构建时间预估

| 操作 | 时间 |
|------|------|
| 创建仓库 | 2分钟 |
| 上传代码 | 5-10分钟 |
| 运行Actions | 5-10分钟 |
| 下载APK | 1分钟 |
| 总计 | 15-30分钟 |

---

## 💡 最佳实践

### 1. 使用Git版本控制

```bash
# 提交前检查
git status

# 查看改动
git diff

# 提交
git add .
git commit -m "描述你的改动"
git push
```

### 2. 分支管理

```bash
# 创建功能分支
git checkout -b feature/new-habit

# 完成后合并到主分支
git checkout main
git merge feature/new-habit
git push
```

### 3. 使用GitHub Desktop

如果不熟悉命令行，可以：
1. 下载 GitHub Desktop
2. 可视化管理仓库
3. 简单拖拽提交

---

## 🐛 常见问题

### Q1: 构建失败怎么办？

**A:**
1. 查看构建日志
2. 找到错误信息
3. 修复问题
4. 重新提交代码

### Q2: 如何构建Release版本？

**A:**
1. 配置keystore签名
2. 运行工作流时选择 `release`
3. 下载 Release APK

### Q3: 构建需要多长时间？

**A:**
- 首次构建：8-12分钟
- 后续构建（使用缓存）：5-8分钟

### Q4: 可以在手机上直接测试吗？

**A:**
可以！下载Debug APK，直接安装到手机测试。

---

## ✅ 完成检查清单

- [ ] 创建GitHub仓库
- [ ] 上传所有项目文件
- [ ] 确认 `.github/workflows/` 目录已上传
- [ ] 运行GitHub Actions
- [ ] 等待构建完成（5-10分钟）
- [ ] 下载APK文件
- [ ] 安装到设备测试
- [ ] 验证所有功能

---

## 📞 需要帮助？

如果遇到问题：

1. **查看GitHub文档：** https://docs.github.com/en/actions
2. **查看构建日志：** 在Actions页面
3. **检查工作流配置：** `.github/workflows/build-apk.yml`

---

## 🎉 开始构建！

准备好了吗？

1. 打开 https://github.com
2. 创建仓库
3. 上传代码
4. 运行Actions
5. 下载APK

**15-30分钟后，你就能拿到APK了！** 🚀
