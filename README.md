# remote-build

远程构建服务：在公开 GitHub-hosted runner（4 核 16G）上，用预构建镜像
`ghcr.io/gg22g2/graalvm-jdk-musl-maven` 执行 Maven / native-image 构建。

- 源码通过带有效期的直链传入（仓库 Secret）
- 构建输出不进入公开日志，完整日志加密打包进产物
- 产物 AES-256 加密 + HMAC 校验，artifact 保留 7 天

## 使用

```bash
# 1) 更新 Secrets（每次构建前）
gh secret set BUILD_SRC_URL      -R <owner>/remote-build   # 粘贴有效期直链
gh secret set BUILD_ZIP_PASSWORD -R <owner>/remote-build   # 粘贴本次产物密码

# 2) 触发构建
gh api repos/<owner>/remote-build/dispatches \
  -f event_type=build-request \
  -f 'client_payload={"build_command":"mvn -B clean package","artifact_path":"target"}'

# 3) 下载并解密（run-id 从触发响应或 Actions 页获取）
gh run download -R <owner>/remote-build <run-id> -n build-output
openssl dgst -sha256 -hmac <密码> bundle.enc | awk '{print $NF}'   # 与 bundle.enc.sha256 比对
openssl enc -d -aes-256-cbc -pbkdf2 -pass pass:<密码> -in bundle.enc -out bundle.tgz
tar -xzf bundle.tgz   # 得到 artifact/ 与 build.log
```
