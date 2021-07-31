package io.horizontalsystems.bankwallet.modules.keycard

import android.nfc.NfcAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import im.status.keycard.android.NFCCardManager
import im.status.keycard.applet.ApplicationInfo
import im.status.keycard.applet.KeycardCommandSet
import im.status.keycard.io.CardChannel
import im.status.keycard.io.CardListener
import io.horizontalsystems.bankwallet.R
import io.horizontalsystems.core.findNavController
import kotlinx.android.synthetic.main.fragment_keycard_pair.*


/**
 * A simple [Fragment] subclass.
 * Use the [KeycardPairFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class KeycardPairFragment : Fragment() {

    private var nfcAdapter: NfcAdapter? = null
    private var cardManager: NFCCardManager? = null
    private var cmdSet: KeycardCommandSet? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        nfcAdapter = NfcAdapter.getDefaultAdapter(context);
        cardManager = NFCCardManager()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_keycard_pair, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
        checkBox.setOnClickListener {
            editTextPUK.isEnabled = !checkBox.isChecked
        }
        cardManager!!.setCardListener(object : CardListener {
            override fun onConnected(cardChannel: CardChannel?) {
                cmdSet = KeycardCommandSet(cardChannel)
                val applet = cmdSet!!.select()
                if (! applet.isOK ) {
                    activity?.runOnUiThread {
                        Toast.makeText(context, "Could not select keycard applet. Are you sure it's a KeyCard?", Toast.LENGTH_SHORT).show()
                    }
                    return
                }
                val info = ApplicationInfo(applet.checkOK().data)
                activity?.runOnUiThread {
                    Toast.makeText(context, "Card connected", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onDisconnected() {
                cmdSet = null
                activity?.runOnUiThread {
                    Toast.makeText(context, "Card disconnected", Toast.LENGTH_SHORT).show()
                }

            }
        })
        cardManager!!.start()
    }

    override fun onPause() {
        super.onPause()
        nfcAdapter?.disableReaderMode(activity)
    }
    override fun onResume() {
        super.onResume()
        nfcAdapter?.enableReaderMode(
            activity,
            cardManager, NfcAdapter.FLAG_READER_NFC_A or NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK, null
        )
    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment KeycardPairFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) {

        }
    }
}