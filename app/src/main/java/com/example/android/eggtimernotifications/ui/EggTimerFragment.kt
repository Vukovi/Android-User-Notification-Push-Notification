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

package com.example.android.eggtimernotifications.ui

import android.app.NotificationChannel
import android.app.NotificationManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.example.android.eggtimernotifications.R
import com.example.android.eggtimernotifications.databinding.FragmentEggTimerBinding
import com.google.firebase.messaging.FirebaseMessaging

class EggTimerFragment : Fragment() {

    private val TOPIC = "breakfast"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding: FragmentEggTimerBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_egg_timer, container, false
        )

        val viewModel = ViewModelProviders.of(this).get(EggTimerViewModel::class.java)

        binding.eggTimerViewModel = viewModel
        binding.lifecycleOwner = this.viewLifecycleOwner

        // pozivam metodu createChannel da bih napravio channel, a zbog IMPORTANCE_HIGH, notifikacija ce se ponasati kao kod Vibera
        createChannel(getString(R.string.egg_notification_channel_id), getString(R.string.egg_notification_channel_name), NotificationManager.IMPORTANCE_HIGH)

        subscribeTopic()
        return binding.root
    }

    /**
     * Da bi se notifikacija u Androidu pokrenula, treba mi Channel
     */
    private fun createChannel(channelId: String, channelName: String, importanceLevel: Int = NotificationManager.IMPORTANCE_LOW) {
        // pravim channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val notificationChannel = NotificationChannel(channelId, channelName, importanceLevel)

            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = "Vreme je za dorucak!"
            notificationChannel.setShowBadge(false) // ako necu da mi ikonica na aplikaciji ima obevestenje o broju pristiglih notifikacija

            // sad mi treba instanca notificationManagera kojoj cu da prosledim ovaj channel
            val notificationManager = requireActivity().getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(notificationChannel)
        }

    }

    companion object {
        fun newInstance() = EggTimerFragment()
    }

    private fun subscribeTopic() {
        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC).addOnCompleteListener { task ->
            var msg = getString(R.string.message_subscribed)
            if (!task.isSuccessful) {
                msg = getString(R.string.message_subscribe_failed)
            }
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        }
    }

}

