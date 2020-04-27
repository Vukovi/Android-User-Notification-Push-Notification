/*
 * Copyright (C) 2019 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.eggtimernotifications.util

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.core.app.NotificationCompat
import com.example.android.eggtimernotifications.MainActivity
import com.example.android.eggtimernotifications.R
import com.example.android.eggtimernotifications.receiver.SnoozeReceiver

// Notification ID.
private val NOTIFICATION_ID = 0
private val REQUEST_CODE = 0
private val FLAGS = 0


// Funkcija Ekstenzije NotificationManagera koja ce slati notifikacije
fun NotificationManager.sendNotification(messageBody: String, applicationContext: Context) {

    // Da bi korisnik mogao da ima interakciju sa notifikacijom prvo treba da napravim intent (email,viber,sms....)
    val contentIntent = Intent(applicationContext, MainActivity::class.java)

    // Sad mi treba PendingIntent pomocu kojeg ce sistem da otvori moju aplikaciju
    val pendingIntent = PendingIntent.getActivity(applicationContext, NOTIFICATION_ID, contentIntent, PendingIntent.FLAG_UPDATE_CURRENT)

    // Stilizovacu notifikaciju, dodavanjem slike
    val eggImage = BitmapFactory.decodeResource(applicationContext.resources, R.drawable.cooked_egg)
    // Postoje razne ugradjene notifikacije, za muziku, za mape itd, ja cu koristiti veliku ugradnu notifikaciju
    val bigNotificationStyle = NotificationCompat.BigPictureStyle()
        .bigPicture(eggImage)
        .bigLargeIcon(null)

    // Sad mi treba novi Intent za Snooze akciju - tj snoozovanu notifikaciju
    val snoozeIntent = Intent(applicationContext, SnoozeReceiver::class.java)
    val snoozePendingIntent = PendingIntent.getBroadcast(applicationContext, REQUEST_CODE, snoozeIntent, FLAGS)

    // Pravim instancu NotificationCompat.Builder
    val builder = NotificationCompat.Builder(applicationContext, applicationContext.getString(R.string.egg_notification_channel_id))
        .setSmallIcon(R.drawable.cooked_egg)
        .setContentTitle(applicationContext.getString(R.string.notification_title))
        .setContentText(messageBody)
        .setContentIntent(pendingIntent) // vracam app iz backgrounda
        .setAutoCancel(true) // kad korisnik otvori notifikaciju ona ce se sama izbrisati
        .setStyle(bigNotificationStyle) // ovde sam rekao da ce notifikacija biti velka ugradna
        .setLargeIcon(eggImage)
        .addAction(R.drawable.common_google_signin_btn_icon_dark, applicationContext.getString(R.string.snooze), snoozePendingIntent) // dodajem notifikaciji button za Snooze notifikaciju
        .setPriority(NotificationCompat.PRIORITY_HIGH) // posto hocu da mi notifikacije iskacu kao kod Vibera, moram i ovde da dodam PRIORITY_HIGH


    // Na kraju pozovem notify da bih okinuo notifikaciju
    notify(NOTIFICATION_ID, builder.build())

}

fun NotificationManager.cancelNotifications() {
    cancelAll()
}
