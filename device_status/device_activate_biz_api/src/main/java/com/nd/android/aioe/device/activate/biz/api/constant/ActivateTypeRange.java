package com.nd.android.aioe.device.activate.biz.api.constant;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.SOURCE;

//@Retention表示这个注解保留的范围，SOURCE=注解将被编译器编译的时候丢弃，不在代码运行时存在，这个注解只是希望IDE警告限定值的范围并不需要保留到VM或者运行时
@Retention(SOURCE)
//@Target 这个注解需要使用的地方 PARAMETER=注解将被使用到方法的参数中
@Target({PARAMETER})
//显式声明被定义的整数值，除了@IntDef还有@LongDef @StringDef等等
@IntDef(value = {ActivateType.TYPE_UC, ActivateType.TYPE_ORG, ActivateType.TYPE_AUTO})
public @interface ActivateTypeRange {

}