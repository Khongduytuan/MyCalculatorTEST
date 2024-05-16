package com.eagletech.test.ui.gallery

//import org.mariuszgromada.math.mxparser.Expression
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.eagletech.test.dataapp.MyDataSharedPreferences
import com.eagletech.test.R
import com.eagletech.test.databinding.FragmentGalleryBinding
import net.objecthunter.exp4j.ExpressionBuilder
import java.text.DecimalFormat
import java.util.EmptyStackException

class GalleryFragment : Fragment() {

    private var _binding: FragmentGalleryBinding? = null
    private val binding get() = _binding!!
    private lateinit var myDataSharedPreferences: MyDataSharedPreferences


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGalleryBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        myDataSharedPreferences = MyDataSharedPreferences.getInstance(requireContext())
        clickButtons()
    }

    private fun clickButtons() {
        binding.btnClear.setOnClickListener {
            binding.tvInput.text = ""
            binding.tvOutput.text = ""
        }

        binding.btnBracketLeft.setOnClickListener {
            binding.tvInput.text = addTextToInput("(")
        }
        binding.btnBracketRight.setOnClickListener {
            binding.tvInput.text = addTextToInput(")")
        }
        binding.btnNum0.setOnClickListener {
            binding.tvInput.text = addTextToInput("0")
        }
        binding.btnNum1.setOnClickListener {
            binding.tvInput.text = addTextToInput("1")
        }
        binding.btnNum2.setOnClickListener {
            binding.tvInput.text = addTextToInput("2")
        }
        binding.btnNum3.setOnClickListener {
            binding.tvInput.text = addTextToInput("3")
        }
        binding.btnNum4.setOnClickListener {
            binding.tvInput.text = addTextToInput("4")
        }
        binding.btnNum5.setOnClickListener {
            binding.tvInput.text = addTextToInput("5")
        }
        binding.btnNum6.setOnClickListener {
            binding.tvInput.text = addTextToInput("6")
        }
        binding.btnNum7.setOnClickListener {
            binding.tvInput.text = addTextToInput("7")
        }
        binding.btnNum8.setOnClickListener {
            binding.tvInput.text = addTextToInput("8")
        }
        binding.btnNum9.setOnClickListener {
            binding.tvInput.text = addTextToInput("9")
        }
        binding.btnDot.setOnClickListener {
            binding.tvInput.text = addTextToInput(".")
        }
        binding.btnDiv.setOnClickListener {
            binding.tvInput.text = addTextToInput("÷") // ALT + 0247
        }
        binding.btnMul.setOnClickListener {
            binding.tvInput.text = addTextToInput("×") // ALT + 0215
        }
        binding.btnSub.setOnClickListener {
            binding.tvInput.text = addTextToInput("-")
        }
        binding.btnAdd.setOnClickListener {
            binding.tvInput.text = addTextToInput("+")
        }

        binding.btnEqual.setOnClickListener {
            if (myDataSharedPreferences.getTimesCalculate() > 0 || myDataSharedPreferences.isPremiumCalculate == true) {
                showResult()
            } else {
                Toast.makeText(
                    requireContext(),
                    "You must purchase additional usage",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun addTextToInput(buttonValue: String): String {
        return "${binding.tvInput.text}$buttonValue"
    }

    private fun formatText(): String {
        var textFormat = binding.tvInput.text.replace(Regex("÷"), "/")
        textFormat = textFormat.replace(Regex("×"), "*")
        return textFormat
    }

    private fun showResult() {
        try {
            val data = formatText()
            Log.d("data", evaluateExpression(data).toString())
            val res = evaluateExpression(data)
            if (res != Double.NaN) {
                binding.tvOutput.text = DecimalFormat("0.######").format(res).toString()
                binding.tvOutput.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.greenM
                    )
                )
            } else {
                binding.tvOutput.text = "Error"
                binding.tvOutput.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
            }

        } catch (e: Exception) {
            binding.tvOutput.text = "Error"
            binding.tvOutput.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
        }
        myDataSharedPreferences.removeTimesCalculate()
    }

    private fun evaluateExpression(expression: String): Double {
        return try {
            val e = ExpressionBuilder(expression).build()
            e.evaluate()
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "Invalid expression", Toast.LENGTH_SHORT).show()
            Double.NaN
        } catch (e: ArithmeticException) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "Arithmetic error in expression", Toast.LENGTH_SHORT)
                .show()
            Double.NaN
        } catch (e: EmptyStackException) {
            e.printStackTrace()
            Toast.makeText(
                requireContext(),
                "Invalid stack operation in expression",
                Toast.LENGTH_SHORT
            ).show()
            Double.NaN
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}