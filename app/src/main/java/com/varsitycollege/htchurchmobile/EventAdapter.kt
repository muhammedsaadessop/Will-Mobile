package com.varsitycollege.htchurchmobile

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class EventAdapter(private val eventList: MutableList<Event>) : RecyclerView.Adapter<EventAdapter.EventViewHolder>(){

    class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val date: TextView = itemView.findViewById(R.id.date)
        val churchName: TextView = itemView.findViewById(R.id.churchName)
        val eventName: TextView = itemView.findViewById(R.id.eventName)
        val description: TextView = itemView.findViewById(R.id.description)
        val startTime: TextView = itemView.findViewById(R.id.starttime)
        val endTime: TextView = itemView.findViewById(R.id.endtime)
        val location: TextView = itemView.findViewById(R.id.location)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.event_recycler_item, parent, false)
        return EventViewHolder(view)
    }
    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = eventList[position]
        holder.date.text = event.date
        holder.churchName.text = event.church
        holder.eventName.text = event.name
        holder.description.text = event.description
        holder.startTime.text = event.startTime
        holder.endTime.text = event.endTime
        holder.location.text = event.location

        //Context menu for the on hold
        holder.itemView.setOnCreateContextMenuListener { menu, v, menuInfo ->
            menu.add(Menu.NONE, R.id.edit_item, Menu.NONE, "Edit")
                .setOnMenuItemClickListener { menuItem ->

                    val intent = Intent(v.context, EditEvent::class.java)
                    intent.putExtra("eventName", event.name)
                    Log.d("EventAdapter", "eventName: ${event.name}")
                    v.context.startActivity(intent)
                    true

                }
            menu.add(Menu.NONE, R.id.delete_item, Menu.NONE, "Delete")
                .setOnMenuItemClickListener { menuItem ->
                    val db = FirebaseFirestore.getInstance()
                    val eventsRef = db.collection("events")
                    event.name?.let { it1 ->
                        Log.d("EventAdapter", "Event name: $it1")
                        eventsRef.document(it1).delete().addOnSuccessListener {
                            Log.d("EventAdapter", "DocumentSnapshot successfully deleted!")
                            eventList.removeAt(position)
                            notifyItemRemoved(position)
                            notifyItemRangeChanged(position, eventList.size)
                            Toast.makeText(v.context, "Event deleted", Toast.LENGTH_SHORT).show()
                        }.addOnFailureListener { e ->
                            Log.w("EventAdapter", "Error deleting document", e)
                        }
                    }
                    true
                }
        }
    }
    override fun getItemCount() = eventList.size


}







