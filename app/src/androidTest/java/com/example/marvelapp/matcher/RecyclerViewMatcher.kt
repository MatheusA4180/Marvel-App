package com.example.marvelapp.matcher

import android.content.res.Resources
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.core.internal.deps.guava.base.Preconditions.checkState
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher

class RecyclerViewMatcher(private val recyclerViewId: Int) {

    fun atPositionOnView(position: Int, targetViewId: Int = UNSPECIFIED): Matcher<View> {
        return object : TypeSafeMatcher<View>() {
            var resources: Resources? = null
            var recycler: RecyclerView? = null
            var holder: RecyclerView.ViewHolder? = null

            override fun matchesSafely(item: View?): Boolean {
                item?.let { view ->
                    resources = view.resources
                    recycler = view.rootView.findViewById(recyclerViewId) ?: return false
                    holder = recycler!!.findViewHolderForAdapterPosition(position) ?: return false
                    return if (targetViewId == UNSPECIFIED) {
                        view == holder!!.itemView
                    } else {
                        view == holder!!.itemView.findViewById(targetViewId)
                    }
                } ?: return false
            }

            private fun getResourceName(id: Int): String? {
                return try {
                    "R.id." + resources?.getResourceEntryName(id)
                } catch (ex: Resources.NotFoundException) {
                    String.format("resource id %s - name not found", id)
                }
            }

            override fun describeTo(description: Description?) {
                checkState(resources != null, "resource should be init by matchesSafely()")
                recycler ?: run {
                    description?.appendText(
                        "RecyclerView with " + getResourceName(recyclerViewId)
                    )
                    return
                }
                holder ?: run {
                    description?.appendText(
                        String.format(
                            "in RecyclerView (%s) at position %s",
                            getResourceName(recyclerViewId), position
                        )
                    )
                    return
                }
                if (targetViewId == UNSPECIFIED) {
                    description?.appendText(
                        String.format(
                            "in RecyclerView (%s) at position %s",
                            getResourceName(recyclerViewId), position
                        )
                    )
                    return
                }
                description?.appendText(
                    String.format(
                        "in RecyclerView (%s) at position %s and with %s",
                        getResourceName(recyclerViewId),
                        position,
                        getResourceName(targetViewId)
                    )
                )
            }

        }
    }

    companion object {
        private const val UNSPECIFIED = -1
    }

}
