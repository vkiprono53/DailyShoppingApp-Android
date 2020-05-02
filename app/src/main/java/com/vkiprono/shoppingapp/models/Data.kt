package com.vkiprono.shoppingapp.models

class Data(val type:String, val amount:Int,val note:String, val date:String,val userId:String) {
    constructor():this("",0,"","","")

}