package com.chilik1020.radiolist

import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TableRow
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

class MainActivity : AppCompatActivity() {

    private val list = mutableListOf<ListItem>()

    //private val radioButtons = mutableListOf<RadioButton>()
    private lateinit var chipGroup: ChipGroup
    private lateinit var radioGroupH: RadioGroup
    private lateinit var radioGroupV: RadioGroup
    private lateinit var customRadioGroupFlow: GridRadioGroup

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        init()
        initViews()
        printDisplaySize()
    }

    private fun init() {
        val titles = listOf<String>("asd", "asdasdad", "as", "asss")
        for (i in 0..5) {
            list.add(ListItem(i, false, titles.shuffled().first()))
        }
    }

    private fun initViews() {
        chipGroup = findViewById(R.id.chipGroup)
        radioGroupH = findViewById(R.id.radioGroupH)
        radioGroupV = findViewById(R.id.radioGroupV)
        customRadioGroupFlow = findViewById(R.id.customRadioGroupFlow)

        val layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        customRadioGroupFlow.orientation = AutoAlignedGridLayout.Orientation.HORIZONTAL
        customRadioGroupFlow.itemSpacing = 50
        customRadioGroupFlow.lineSpacing = 50

        for (item in list) {
            val rb = RadioButton(this)
            rb.text = item.title
            rb.id = item.id
            rb.isChecked = item.checked
            rb.layoutParams = layoutParams
            customRadioGroupFlow.addView(rb)
        }

        for (item in list) {
            val chip = Chip(this)
            chip.text = item.title
            chip.id = item.id
            chip.isChecked = item.checked
            chip.layoutParams = layoutParams
            chipGroup.addView(chip)
        }

        for (item in list) {
            val rb = RadioButton(this)
            rb.text = item.title
            rb.id = item.id
            rb.isChecked = item.checked
            rb.layoutParams = layoutParams
            radioGroupH.addView(rb)
        }

        for (item in list) {
            val rb = RadioButton(this)
            rb.text = item.title
            rb.id = item.id
            rb.isChecked = item.checked
            rb.layoutParams = layoutParams
            radioGroupV.addView(rb)
        }

        val tableRow = TableRow(this)

        for (item in list) {
            val rb = RadioButton(this)
            rb.text = item.title
            rb.id = item.id
            rb.isChecked = item.checked
            rb.layoutParams = layoutParams
            tableRow.addView(rb)
        }

        customRadioGroupFlow.setOnCheckedChangeListener(object :
            GridRadioGroup.OnCheckedChangeListener {
            override fun onCheckedChanged(group: GridRadioGroup, checkedId: Int) {
                Log.d("___)", "CRGF : id = $checkedId")
            }
        })
    }

    private fun printDisplaySize() {
        val width = this.resources.displayMetrics.widthPixels
        Log.d("___)", "Display WIDTH = $width")
    }
}