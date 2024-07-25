package kky.flab.lookaround.feature.home

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlin.math.min

class MapBehavior(context: Context, attrs: AttributeSet) : CoordinatorLayout.Behavior<FrameLayout>(context, attrs) {

    override fun onLayoutChild(
        parent: CoordinatorLayout,
        child: FrameLayout,
        layoutDirection: Int
    ): Boolean {
        parent.onLayoutChild(child, layoutDirection)

        val dependencies = parent.getDependencies(child)
        for (dependency in dependencies) {
            try {
                BottomSheetBehavior.from(dependency)
                onDependentViewChanged(parent, child, dependency)
            } catch (e: IllegalArgumentException) {
                Log.e("MovedownBehavior", e.message ?: "Invalid dependency view")
                continue
            }
        }

        return true
    }

    override fun layoutDependsOn(
        parent: CoordinatorLayout,
        child: FrameLayout,
        dependency: View
    ): Boolean = dependency is ConstraintLayout

    override fun onDependentViewChanged(
        parent: CoordinatorLayout,
        child: FrameLayout,
        dependency: View
    ): Boolean {
        val translationY = min(0, dependency.top - child.height) + dpToPx(10, child.context)
        child.translationY = translationY.toFloat()
        return false
    }

    private fun dpToPx(dp: Int, context: Context): Int {
        return (dp * context.resources.displayMetrics.density).toInt()
    }
}