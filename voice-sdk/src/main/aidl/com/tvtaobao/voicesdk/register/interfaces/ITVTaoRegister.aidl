// ITVTaoRegister.aidl
package com.tvtaobao.voicesdk.register.interfaces;
import  com.tvtaobao.voicesdk.register.bo.Register;


interface ITVTaoRegister {
   void onRegister(in Register register);
   void unRegister(in Register register);
}
