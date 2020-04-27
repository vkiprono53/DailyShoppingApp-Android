package com.vkiprono.shoppingapp.models

class User(val userName:String, val email:String, val imgUrl:String) {
    constructor():this("","","")
}