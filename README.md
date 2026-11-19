# 期中实验 NotePad 记事本应用

移动软件开发课程实验，作者：王鑫懿（学号 121052024077）。

在课程提供的 NotePad 示例基础上扩展功能：列表增加时间戳、支持按标题或正文查询，另外自选了三项扩展功能。

## 主要内容

- 基本要求一：列表每条笔记显示“修改于 yyyy-MM-dd HH:mm”时间戳
- 基本要求二：SearchView 实时按标题或正文模糊查询，置顶优先、再按修改时间排序
- 扩展一：分类颜色，五档颜色在列表左侧以色条显示
- 扩展二：长按置顶/取消置顶，排序时置顶笔记始终靠前
- 扩展三：ACTION_SEND 把笔记以纯文本分享到其它应用

## 如何运行

1. 用 Android Studio 打开本目录（不要打开上层文件夹）
2. 首次 Sync 需要联网下载 Gradle 8.7 与依赖；国内网络若拉不动 Google Maven，可保留 `settings.gradle` 里的阿里云镜像
3. 选择模拟器或真机，运行 `app`

环境配置：Android Gradle Plugin 8.5.2 / Gradle 8.7 / compileSdk 34 / minSdk 24 / JDK 17

## 目录

```
app/src/main/java/        源码
app/src/main/res/         布局、字符串、图片等资源
docs/                     实验报告（Word）
```

## 说明

数据存在应用私有目录的 SQLite（notepad.db，表 notes），卸载重装会清空。
