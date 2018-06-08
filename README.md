# NumberInputView

## 功能
接收键盘输入的数字短信验证码
## 原型
[点我到简书](https://www.jianshu.com/p/91b0b8038dd5)
## 特点
随输入的字符产生动画效果（如下图）  
额，当然，图有点糊了，看的不是很清楚。  
分两个场景，输入和删除  

##### 输入
当用户输入一个数字的时大概有两个效果：  
1. 文字alpha由全透明变成不透明  
2. 指示底线从中间向两边发生颜色渐变  
##### 删除
当用户删除一个数字的时大概有两个效果：  
1. 文字alpha由不透明变成透明（消失）
2. 指示底线从两边向中间发生颜色渐变

## Demo
![我是demo](https://raw.githubusercontent.com/jayyuz/MessageVerifyDemo/master/images/20180605223832.gif)

## 如何使用？
在xml中这样写：
```xml
   <com.jaesoon.messageverifydemo.widget.NumberInputView
        android:id="@+id/numberInputView"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_gravity="center"
        android:layout_marginTop="25dp"
        android:orientation="horizontal"
        android:padding="0dp" />
```
在java中这样获取用户输入的值：
```java
String msg = numberInputView.getText();
```

**重要：如果要用在生产上，请注意keep这个View，否则会导致混淆之后无法使用**

## 定制
提供了以下属性，方便大家进行定制  
```xml
    <declare-styleable name="NumberInputView">
        <attr name="activeColor" format="color" />
        <attr name="inactiveColor" format="color" />
        <attr name="numberColor" format="color" />
        <attr name="numberTextSize" format="dimension" />
        <attr name="spacing" format="dimension" />
        <attr name="bottomLineWidth" format="dimension" />
        <attr name="digit" format="integer" />
    </declare-styleable>
```