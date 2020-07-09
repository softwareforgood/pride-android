package com.softwareforgood.pridefestival.ui.info

import android.content.Context
import android.util.AttributeSet
import android.view.View
import com.andrewreitz.velcro.betterviewanimator.BetterViewAnimator
import com.softwareforgood.pridefestival.R
import com.softwareforgood.pridefestival.databinding.ActivityInfoBinding
import com.softwareforgood.pridefestival.util.component
import javax.inject.Inject

interface InfoView {
    val tryAgainButton: View
    fun showInfo(infoText: String)
    fun showError()
    fun showSpinner()
}

class DefaultInfoView(
    context: Context,
    attrs: AttributeSet
) : BetterViewAnimator(context, attrs), InfoView {

    override val tryAgainButton: View get() = binding.infoError

    // This is done because the view layout is just implemented in the
    // activity's xml.
    @Inject lateinit var binding: ActivityInfoBinding
    @Inject lateinit var presenter: InfoPresenter

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        component<InfoComponent>().inject(this)
        presenter.attachView(this)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        presenter.detachView()
    }

    override fun showError() {
        displayedChildId = R.id.info_error
    }

    override fun showSpinner() {
        displayedChildId = R.id.info_progress
    }

    override fun showInfo(infoText: String) {
        // not the prettiest of solutions but gotta take what you're given.
        // use base 64 images to make it easier to load the images into the Webview.
        // Everything goes into the webview to ensure color and sizes match what's on parse.
        binding.infoText.loadData("""
            <head>
              <style>
                .row {
                  display: table;
                  border-collapse: collapse;
                  width: 100%;
                }
                .item {
                  display: table-cell;
                  vertical-align: top;
                  padding: 8px;
                }
                .item img {
                  display: block;
                  width: 100%;
                  height: auto;
                }
              </style>
            </head>
            <body>
                <font size="34" color="#7f3f97"><b>Connect with us</b></font>
                <div class="row">
                    <div class="item">
                        <a href="https://www.facebook.com/tcpride/"><img src="data:image/png;base64, ${context.getString(R.string.facebook_base64_image)}" alt="find us on facebook" /></a>
                    </div>
                    <div class="item">
                        <a href="https://www.instagram.com/twincitiespride/"><img src="data:image/png;base64, ${context.getString(R.string.instagram_base64_image)}" alt="find us on instagram" /></a>
                    </div>
                    <div class="item">
                        <a href="http://tcpride.tumblr.com"><img src="data:image/png;base64, ${context.getString(R.string.tumblr_base64_image)}" alt="find us on tumblr" /></a>
                    </div>
                    <div class="item">
                        <a href="https://twitter.com/TwinCitiesPride"><img src="data:image/png;base64, ${context.getString(R.string.twitter_base64_image)}" alt="find us on twitter" /></a>
                    </div>
                    <div class="item">
                        <a href="https://www.youtube.com/user/TwinCitiesPride"><img src="data:image/png;base64, ${context.getString(R.string.youtube_base64_image)}" alt="find us on youtube" /></a>
                    </div>
                </div>
                $infoText
            </body>
            """, "text/html", "utf-8"
        )
        displayedChildId = R.id.info_text
    }
}
