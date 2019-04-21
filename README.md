# NaviBand-Andriod

## 背景

帮助同学的创业项目 `导航手环` 制作的DEMO。该项目旨在用手环向用户发送导航指令，以帮助听觉障碍者在不需要查看屏幕的情况下获取导航信息，
例如用户在某路口需要左转时，用户左手手腕的手环会产生震动。

## DEMO 思路 

使用 `java` 进行Native开发，使用 [高德地图-Android导航SDK](https://lbs.amap.com/api/android-navi-sdk/summary/) 处理路径规划与导航逻辑。

在导航进行的过程中，响应相关回调获取由SDK提供的 [`NaviInfo`](http://a.amap.com/lbs/static/unzip/Android_Navi_Doc/index.html) 对象, 在出现转弯
事件后，通过蓝牙控制手环震动。

本DEMO的手环硬件部分使用 `stm32f103c8tx` 处理器配合简单的硬件实现，亦由本人开发，
请参见[NaviBand-Hardware](https://github.com/CNLHC/Naviband-Hardware/tree/master)
