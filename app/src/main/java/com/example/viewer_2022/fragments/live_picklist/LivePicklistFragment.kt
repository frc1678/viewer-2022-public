package com.example.viewer_2022.fragments.live_picklist

import android.app.AlertDialog
import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.viewer_2022.PicklistConnectionManager
import com.example.viewer_2022.R
import com.example.viewer_2022.databinding.FragmentLivePicklistBinding
import com.example.viewer_2022.databinding.PicklistEditPopupBinding
import com.google.gson.Gson
import com.google.gson.JsonParser
import kotlinx.coroutines.launch
import kotlin.properties.Delegates


/**
 * The fragment for the live team orderings from the picklist.
 *
 * @see R.layout.fragment_live_picklist
 */
class LivePicklistFragment : Fragment() {

    private lateinit var binding: FragmentLivePicklistBinding

    private lateinit var adapter: LivePicklistRecyclerAdapter

    /** The list of teams with their picklist rankings, sorted by their rank. */
    var order: List<String> = listOf()
    var dnp: List<String> = listOf()

    var canEdit: Boolean by Delegates.observable(false) { _, _, newValue ->
        if (newValue) {
            binding.btnPicklistEdit.text = getText(R.string.btn_picklist_view)
        } else {
            binding.btnPicklistEdit.text = getText(R.string.btn_picklist_edit)
        }
    }

    /** Runs when the fragment is created, to inflate the layout. */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the picklist layout.
        binding = FragmentLivePicklistBinding.inflate(inflater, container, false)
        // Generate and show the list.
        initAdapter()
        updateOrder()
        setupEditButton()
        setupWebSocket()
        setupConnectionMonitor()


        return binding.root
    }

    private fun setupConnectionMonitor() {
        PicklistConnectionManager.addConnectionListener {
            if (it) {
                binding.btnConnection.text = getText(R.string.connected)
                binding.btnConnection.setTextColor(resources.getColor(R.color.ElectricGreen))
            } else {
                binding.btnConnection.text = getText(R.string.disconnected)
                binding.btnConnection.setTextColor(resources.getColor(R.color.Red))
                canEdit = false
            }
        }
        binding.btnConnection.setOnClickListener {
            if (!PicklistConnectionManager.connected) {
                Log.d("picklist", "connection button clicked. attempting to connect")
                lifecycleScope.launch {
                    PicklistConnectionManager.connect()
                }
            }
        }

    }

    private fun setupEditButton() {
        binding.btnPicklistEdit.setOnClickListener {
            if (PicklistConnectionManager.connected) {
                if (canEdit) {
                    lifecycleScope.launch {
                        val data = LogoutRequest()
                        val dataText = Gson().toJson(data)
                        PicklistConnectionManager.send(dataText)
                    }
                    canEdit = false
                } else {
                    EditPopup {
                        Log.d("picklist", "picklist password: $it")
                        lifecycleScope.launch {
                            val data = LoginRequest(password = it)
                            val dataText = Gson().toJson(data)
                            PicklistConnectionManager.send(dataText)
                        }
                    }.show(fragmentManager!!, "edit_popup")
                }
            } else {
                Toast.makeText(
                    context,
                    "You must be connected to the picklist to edit.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    /**
     * Initializes the adapter of the [ListView][android.widget.ListView].
     */
    private fun initAdapter() {
//        binding.lvLivePicklist.adapter = LivePicklistAdapter(
//            context = context!!,
//            fragment = this
//        )
        itemTouchHelper.attachToRecyclerView(binding.rvLivePicklist)
        adapter = LivePicklistRecyclerAdapter(this)
        binding.rvLivePicklist.layoutManager = LinearLayoutManager(context)
        binding.rvLivePicklist.adapter = adapter
    }

    /**
     * Updates the text styles of the header buttons based on the current ordering. Called when the
     * order is changed.
     */
    private fun updateOrder() {
        Log.d("picklist", "updating headers")
        adapter.submitList(order + dnp)
        adapter.notifyDataSetChanged()
    }

    private fun setupWebSocket() {
        lifecycleScope.launch {
            Log.d("picklist", "Picklist connection success: ${PicklistConnectionManager.connect()}")

        }
        PicklistConnectionManager.addMessageListener {
            Log.d("picklist", "Received message text: $it")
            val messageJson = JsonParser.parseString(it).asJsonObject

            when (messageJson.getAsJsonPrimitive("type").asString) {
                "picklist_data" -> {
                    Log.d("picklist", "received picklist data")
                    val picklistArr =
                        messageJson.getAsJsonArray("ranking").asJsonArray.toList()
                            .map { teamNumber -> return@map teamNumber.asString }
                    val dnpArr = messageJson.getAsJsonArray("dnp").asJsonArray.toList()
                        .map { teamNumber -> return@map teamNumber.asString }
                    order = picklistArr
                    dnp = dnpArr
                    updateOrder()
                }
                "login" -> {
                    Log.d("picklist", "received picklist data")
                    val loginSuccess = messageJson.getAsJsonPrimitive("success").asBoolean
                    if (loginSuccess) loginSuccess() else loginFailed()

                }
            }
        }

    }

    private fun loginSuccess() {
        Log.d("picklist", "login success")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val toast = Toast.makeText(
                context,
                Html.fromHtml(
                    "<font color='#24850f'>Login successful</font>",
                    Html.FROM_HTML_MODE_COMPACT
                ),
                Toast.LENGTH_SHORT
            )
            toast.show()
        }
        canEdit = true
    }

    private fun loginFailed() {
        Log.d("picklist", "login failed")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val toast = Toast.makeText(
                context,
                Html.fromHtml(
                    "<font color='#e61c0e'>Login failed</font>",
                    Html.FROM_HTML_MODE_COMPACT
                ),
                Toast.LENGTH_SHORT
            )
            toast.show()

        }
    }

    private val itemTouchHelper by lazy {
        val simpleItemTouchCallback = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.START or ItemTouchHelper.END,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            var lastMove: LastMove? = null
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {

                val from = viewHolder.adapterPosition
                val to = target.adapterPosition
                Log.d("picklist", "Move event: from: $from to: $to")

                lastMove?.to = to
                if ((lastMove != null) and !lastMove!!.fromSet) {
                    lastMove?.fromSet = true
                    lastMove?.from = from
                }

                Log.d("picklist", "LastMove: $lastMove")

                adapter.notifyItemMoved(from, to)

                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val picklistViewHolder =
                    viewHolder as LivePicklistRecyclerAdapter.LivePicklistViewHolder
                Log.d("picklist", "Swiped direction: $direction")
                adapter.handleDNPToggle(picklistViewHolder.adapterPosition)

            }

            override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
                super.onSelectedChanged(viewHolder, actionState)
                viewHolder as LivePicklistRecyclerAdapter.LivePicklistViewHolder?
                if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
                    Log.d("picklist", "started drag")
                    lastMove = LastMove(0, 0)
                    viewHolder?.itemView?.alpha = 0.5f
                }

                if (actionState == ItemTouchHelper.ACTION_STATE_IDLE) {
                    Log.d("picklist", "Done dragging")
                    this.lastMove?.let {
                        adapter.handleOrderChange(it.from, it.to)
                        this.lastMove = null
                    }
                }
            }

            override fun clearView(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ) {
                super.clearView(recyclerView, viewHolder)

                viewHolder.itemView.alpha = 1.0f

            }

            override fun isLongPressDragEnabled(): Boolean =
                canEdit && PicklistConnectionManager.connected

            override fun isItemViewSwipeEnabled(): Boolean =
                canEdit && PicklistConnectionManager.connected

        }


        ItemTouchHelper(simpleItemTouchCallback)
    }
}

class EditPopup(val onSuccess: ((text: String) -> Unit)? = null) : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)

            val inflater = requireActivity().layoutInflater;

            val view = PicklistEditPopupBinding.inflate(inflater, null, false)

            builder.setView(view.root)
                .setPositiveButton("Login") { dialog, id ->
                    onSuccess?.let {
                        it(view.etPicklistPassword.text.toString())
                    }
                }
                .setNegativeButton("Cancel") { dialog, id ->
                    dialog.cancel()
                }

            builder.setTitle("Edit Mode")
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}


data class UpdateDataRequest(
    val type: String = "picklist_update",
    val to_place: Int,
    val from_place: Int
)

data class ToggleDNPRequest(val type: String = "dnp_update", val team_number: Int)
data class LoginRequest(val type: String = "start_edit", val password: String)
data class LogoutRequest(val type: String = "stop_edit")

data class LastMove(var from: Int, var to: Int, var fromSet: Boolean = false)
