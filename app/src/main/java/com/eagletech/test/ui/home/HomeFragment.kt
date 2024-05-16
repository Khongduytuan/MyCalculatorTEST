package com.eagletech.test.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.amazon.device.drm.LicensingService
import com.amazon.device.iap.PurchasingListener
import com.amazon.device.iap.PurchasingService
import com.amazon.device.iap.model.ProductDataResponse
import com.amazon.device.iap.model.PurchaseResponse
import com.amazon.device.iap.model.UserDataResponse
import com.amazon.device.iap.model.FulfillmentResult
import com.amazon.device.iap.model.PurchaseUpdatesResponse
import com.eagletech.test.dataapp.MyDataSharedPreferences
import com.eagletech.test.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var myDataSharedPreferences: MyDataSharedPreferences
    private lateinit var currentUserId: String
    private lateinit var currentMarketplace: String


    // Phải thêm sku các gói vào ứng dụng
    companion object {
        const val sub5Times = "com.eagletech.test.buy5use"
        const val sub10Times = "com.eagletech.test.buy10use"
        const val sub15Times = "com.eagletech.test.buy15use"
        const val sub = "com.eagletech.test.subscribe"
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        myDataSharedPreferences = MyDataSharedPreferences.getInstance(requireContext())
        setupIAPOnCreate()
        setClickItems()
    }

    private fun setClickItems() {
        binding.btn5Times.setOnClickListener {
//            myDataSharedPreferences.addTimesCalculate(3)
            PurchasingService.purchase(sub5Times)
        }
        binding.btn10Times.setOnClickListener {
            PurchasingService.purchase(sub10Times)
        }
        binding.btn15Times.setOnClickListener {
            PurchasingService.purchase(sub15Times)
        }
        binding.btnSubscribe.setOnClickListener {
//            myDataSharedPreferences.isPremiumCalculate = true
            PurchasingService.purchase(sub)
        }


    }


    private fun setupIAPOnCreate() {
        val purchasingListener: PurchasingListener = object : PurchasingListener {
            override fun onUserDataResponse(response: UserDataResponse) {
                when (response.requestStatus!!) {
                    UserDataResponse.RequestStatus.SUCCESSFUL -> {
                        currentUserId = response.userData.userId
                        currentMarketplace = response.userData.marketplace
                        myDataSharedPreferences.currentUserId(currentUserId)
                        Log.v("IAP SDK", "loaded userdataResponse")
                    }

                    UserDataResponse.RequestStatus.FAILED, UserDataResponse.RequestStatus.NOT_SUPPORTED ->
                        Log.v("IAP SDK", "loading failed")
                }
            }

            override fun onProductDataResponse(productDataResponse: ProductDataResponse) {
                when (productDataResponse.requestStatus) {
                    ProductDataResponse.RequestStatus.SUCCESSFUL -> {
                        val products = productDataResponse.productData
                        for (key in products.keys) {
                            val product = products[key]
                            Log.v(
                                "Product:", String.format(
                                    "Product: %s\n Type: %s\n SKU: %s\n Price: %s\n Description: %s\n",
                                    product!!.title,
                                    product.productType,
                                    product.sku,
                                    product.price,
                                    product.description
                                )
                            )
                        }
                        //get all unavailable SKUs
                        for (s in productDataResponse.unavailableSkus) {
                            Log.v("Unavailable SKU:$s", "Unavailable SKU:$s")
                        }
                    }

                    ProductDataResponse.RequestStatus.FAILED -> Log.v("FAILED", "FAILED")
                    else -> {}
                }
            }

            override fun onPurchaseResponse(purchaseResponse: PurchaseResponse) {
                when (purchaseResponse.requestStatus) {
                    PurchaseResponse.RequestStatus.SUCCESSFUL -> {

                        if (purchaseResponse.receipt.sku == sub5Times) {
                            myDataSharedPreferences.addTimesCalculate(5)
                        }
                        if (purchaseResponse.receipt.sku == sub10Times) {
                            myDataSharedPreferences.addTimesCalculate(10)
                        }
                        if (purchaseResponse.receipt.sku == sub15Times) {
                            myDataSharedPreferences.addTimesCalculate(15)
                        }

                        PurchasingService.notifyFulfillment(
                            purchaseResponse.receipt.receiptId,
                            FulfillmentResult.FULFILLED
                        )

                        myDataSharedPreferences.isPremiumCalculate = !purchaseResponse.receipt.isCanceled
                        Log.v("FAILED", "FAILED")
                    }

                    PurchaseResponse.RequestStatus.FAILED -> {}
                    else -> {}
                }
            }

            override fun onPurchaseUpdatesResponse(response: PurchaseUpdatesResponse) {
                // Process receipts
                when (response.requestStatus) {
                    PurchaseUpdatesResponse.RequestStatus.SUCCESSFUL -> {
                        for (receipt in response.receipts) {
                            myDataSharedPreferences.isPremiumCalculate = !receipt.isCanceled
                        }
                        if (response.hasMore()) {
                            PurchasingService.getPurchaseUpdates(false)
                        }

                    }

                    PurchaseUpdatesResponse.RequestStatus.FAILED -> Log.d("FAILED", "FAILED")
                    else -> {}
                }
            }
        }
        PurchasingService.registerListener(requireContext(), purchasingListener)
        Log.d(
            "DetailBuyAct",
            "Appstore SDK Mode: " + LicensingService.getAppstoreSDKMode()
        )
    }

    override fun onResume() {
        super.onResume()
        PurchasingService.getUserData()
        val productSkus: MutableSet<String> = HashSet()
        productSkus.add(sub)
        productSkus.add(sub5Times)
        productSkus.add(sub10Times)
        productSkus.add(sub15Times)
        PurchasingService.getProductData(productSkus)
        PurchasingService.getPurchaseUpdates(false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}