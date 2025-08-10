package io.kitsuri.mayape.manager

import android.app.Activity
import android.os.Build
import android.view.WindowManager

class NativeBlurManager {
    companion object {
        fun applyBlur(activity: Activity, enable: Boolean) {
            val window = activity.window
            val params = window.attributes

            if (enable) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    params.flags = params.flags or
                            WindowManager.LayoutParams.FLAG_BLUR_BEHIND or
                            WindowManager.LayoutParams.FLAG_DIM_BEHIND
                    params.blurBehindRadius = 15
                    params.dimAmount = 0.2f
                } else {
                    params.flags = params.flags or WindowManager.LayoutParams.FLAG_DIM_BEHIND
                    params.dimAmount = 0.3f
                }
            } else {
                params.flags = params.flags and
                        (WindowManager.LayoutParams.FLAG_BLUR_BEHIND or
                                WindowManager.LayoutParams.FLAG_DIM_BEHIND).inv()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    params.blurBehindRadius = 0
                }
                params.dimAmount = 0f
            }

            window.attributes = params
        }

        fun isNativeBlurSupported(): Boolean {
            return Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
        }
    }
}