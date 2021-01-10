package com.dev_candra.stacknotification

/*
Kelas ini adalah sebuah kelas holder yang bertugas untuk menyimpan data id, sender, dan message. Kita perlu memperhatikan baris ini pada MainActivity.
 */
data class NotificationItem (
    var id: Int,
    var sender: String?,
    var message: String?
)