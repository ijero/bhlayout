package cn.ijero.bhlayout

import android.content.Context
import android.support.v4.view.ViewCompat
import android.support.v4.widget.ViewDragHelper
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import cn.ijero.bhlayout.library.R

/**
 *
 * Created by Jero on 2017/11/21.
 */
class BHLayout
@JvmOverloads
constructor(ctx: Context, attrs: AttributeSet? = null)
    : FrameLayout(ctx, attrs) {
    private var rootWidth = 0
    private var rootHeight = 0
    private var mHomeContentView: View? = null
    private var mHomeHeaderView: View? = null
    private var mRightContentView: View? = null
    private lateinit var mHelper: ViewDragHelper
    private var onStateChangeListener: OnStateChangeListener? = null

    // header是否处于展开状态
    private var expanded = false
    // 展开的高度
    private var expandedHeight = 0
    // 收起的高度
    private var unexpandedHeight = 0
    // 是否处于滑动状态
    private var isDragging = false
    // 是否是上下滑动状态
    private var isVerticalDragging = false
    // 是否是左右滑动状态
    private var isHorizontalDragging = false
    // 实时滑动的回调接口对象
    private var onDragBHLayoutCallback: OnDragBHLayoutCallback? = null
    // 保存滑动结束的状态，用于动画完毕后处理回调方法
    private var endView: View? = null
    private var endState = State.STATE_IDLE
    private var endSnapState = SnapState.SNAP_NONE
    private var endDx = 0
    private var endDy = 0
    // 保存当前的state
    private var curState = State.STATE_IDLE
    private var curSnapState = SnapState.SNAP_NONE
    private var isNeedCallBackForEnd = true

    private var gesture: GestureDetector
    private var gestureListener = object : GestureDetector.SimpleOnGestureListener() {
        override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean {
            if (onDragBHLayoutCallback?.onDragBHLayoutEnable() == true && !expanded && distanceY > 20) {
                // 如果是未展开状态，并且是向上滑
                return false
            } else if (onDragBHLayoutCallback?.onDragBHLayoutEnable() == true) {
                return true
            }
            return false
        }
    }

    private val callback = object : ViewDragHelper.Callback() {
        override fun tryCaptureView(child: View, pointerId: Int): Boolean {
            return child == mHomeContentView || child == mRightContentView
        }

        override fun onViewCaptured(capturedChild: View, activePointerId: Int) {
            resetDraggingState()
            isNeedCallBackForEnd = false
        }

        override fun onViewReleased(releasedChild: View, xvel: Float, yvel: Float) {
            resetEndState()
            isNeedCallBackForEnd = true
            endView = releasedChild
            if (releasedChild == mHomeContentView) {
                if ((releasedChild.left < -rootWidth / 2 || xvel < -300) && isHorizontalDragging) {
                    // 左右滑动达到切换条件
                    endSnapState = SnapState.SNAP_TO_RIGHT
                    endDx = rootWidth
                    mHelper.smoothSlideViewTo(releasedChild, -endDx, expandedHeight)
                    ViewCompat.postInvalidateOnAnimation(this@BHLayout)
                } else if (releasedChild.top < (expandedHeight + unexpandedHeight) / 2 || yvel < -300 && isVerticalDragging) {
                    // 上下滑动达到切换状态条件
                    mHelper.smoothSlideViewTo(releasedChild, 0, unexpandedHeight)
                    ViewCompat.postInvalidateOnAnimation(this@BHLayout)
                    endSnapState = SnapState.SNAP_HOME_UNEXPANDED
                    expanded = false
                } else {
                    // 没有达到切换条件，复原
                    mHelper.smoothSlideViewTo(releasedChild, 0, expandedHeight)
                    ViewCompat.postInvalidateOnAnimation(this@BHLayout)
                    endSnapState = SnapState.SNAP_HOME_EXPANDED
                    endDy = expandedHeight - unexpandedHeight
                    expanded = true
                }
            } else if (releasedChild == mRightContentView) {
                if (releasedChild.left > releasedChild.measuredWidth / 2 || xvel > 300) {
                    // 左右滑动达到切换条件
                    mHelper.smoothSlideViewTo(releasedChild, releasedChild.measuredWidth, unexpandedHeight)
                    ViewCompat.postInvalidateOnAnimation(this@BHLayout)
                    endSnapState = SnapState.SNAP_HOME_EXPANDED
                    endDy = expandedHeight - unexpandedHeight
                } else {
                    // 没有达到切换条件，复原
                    mHelper.smoothSlideViewTo(releasedChild, 0, unexpandedHeight)
                    ViewCompat.postInvalidateOnAnimation(this@BHLayout)
                    endSnapState = SnapState.SNAP_TO_RIGHT
                    endDx = rootWidth
                }
            }
            curSnapState = endSnapState
            resetDraggingState()
        }

        override fun clampViewPositionHorizontal(child: View, left: Int, dx: Int): Int {
            if (child == mHomeContentView && left > 5) {
                // 不允许主页界面向右滑动
                return 0
            } else if (child == mRightContentView && left < 5) {
                // 不允许右侧界面向左滑动
                return 0
            }
            return left
        }

        override fun clampViewPositionVertical(child: View, top: Int, dy: Int): Int {
            if (child == mHomeContentView) {
                if (top in (unexpandedHeight + 1)..(expandedHeight - 1)) {
                    // 限定主页界面只能在头布局高度范围内滑动
                    return top
                }
            }
            return child.top
        }

        override fun getViewVerticalDragRange(child: View) = expandedHeight - unexpandedHeight

        override fun onViewPositionChanged(changedView: View, left: Int, top: Int, dx: Int, dy: Int) {
            if (!isDragging) {
                if (Math.abs(dx) > Math.abs(dy) && expanded) {
                    // 左右滑动，并且header处于展开状态
                    isHorizontalDragging = true
                    leftDrag(left, changedView)
                } else {
                    isVerticalDragging = true
                    // 上下滑动
                    topDrag(top, changedView)
                }
            } else {
                if (isHorizontalDragging && expanded) {
                    leftDrag(left, changedView)
                } else {
                    topDrag(top, changedView)
                }
            }
            isDragging = true
        }

        /**
         * 上下滑动
         *
         * @author Jero
         */
        private fun topDrag(dragTop: Int, changedView: View) {
            if (changedView != mHomeContentView) {
                mRightContentView?.layout(0, unexpandedHeight, rootWidth + mRightContentView!!.measuredWidth, mRightContentView!!.height + unexpandedHeight)
                return
            }
            changedView.layout(0, dragTop, changedView.measuredWidth, changedView.measuredHeight + dragTop)
            mHomeHeaderView?.layout(0, dragTop - expandedHeight, mHomeHeaderView!!.measuredWidth, (mHomeHeaderView!!.measuredHeight + dragTop - expandedHeight).toInt())
            curState = State.STATE_CHANGING_VERTICAL
            onStateChangeListener?.onStateChange(changedView, curState, dy = dragTop - unexpandedHeight)
        }

        /**
         * 左右滑动
         *
         * @author Jero
         */
        private fun leftDrag(dragLeft: Int, changedView: View) {
            val headerOffset = dragLeft * ((expandedHeight - unexpandedHeight).toFloat() / changedView.measuredWidth)
            var dx = 0
            var dy = 0
            if (changedView == mHomeContentView) {
                changedView.layout(dragLeft, expandedHeight, changedView.measuredWidth + dragLeft, changedView.measuredHeight + expandedHeight)
                mHomeHeaderView?.layout(0, headerOffset.toInt(), mHomeHeaderView!!.measuredWidth, (mHomeHeaderView!!.measuredHeight + headerOffset).toInt())
                mRightContentView?.layout(rootWidth + dragLeft, unexpandedHeight, rootWidth + dragLeft + mRightContentView!!.measuredWidth, mRightContentView!!.height + unexpandedHeight)
                dx = -dragLeft
                // dy = 展开的高度减去未展开的高度（等于两者之间的距离），再加上ui位移的位置（将复数变为正数）
                dy = expandedHeight - unexpandedHeight + headerOffset.toInt()
            } else if (changedView == mRightContentView) {
                mHomeContentView?.layout(dragLeft - rootWidth, expandedHeight, dragLeft, mHomeContentView!!.measuredHeight + expandedHeight)
                changedView.layout(dragLeft, unexpandedHeight, changedView.measuredWidth + dragLeft, changedView.measuredHeight + expandedHeight)
                mHomeHeaderView?.layout(0, headerOffset.toInt() - expandedHeight + unexpandedHeight, mHomeHeaderView!!.measuredWidth, (mHomeHeaderView!!.measuredHeight + headerOffset).toInt() - expandedHeight + unexpandedHeight)
                dx = rootWidth - dragLeft
                dy = headerOffset.toInt()
            }
            curState = State.STATE_CHANGING_HORIZONTAL
            onStateChangeListener?.onStateChange(changedView, curState, dx = dx, dy = dy)
        }

        override fun getViewHorizontalDragRange(child: View) = rootWidth
    }

    private fun resetDraggingState() {
        isDragging = false
        isVerticalDragging = false
        isHorizontalDragging = false
    }

    private fun resetEndState() {
        endState = State.STATE_IDLE
        endDx = 0
        endSnapState = SnapState.SNAP_NONE
        endDy = 0
    }

    init {
        mHelper = ViewDragHelper.create(this, 1.0F, callback)
        gesture = GestureDetector(context, gestureListener)
        applyStyle(attrs)
    }

    private fun applyStyle(attrs: AttributeSet?) {
        attrs ?: return
        val ta = context.obtainStyledAttributes(attrs, R.styleable.BHLayout)
        expanded = ta.getBoolean(R.styleable.BHLayout_expanded, expanded)
        expandedHeight = ta.getDimensionPixelSize(R.styleable.BHLayout_expandedHeight, expandedHeight)
        unexpandedHeight = ta.getDimensionPixelSize(R.styleable.BHLayout_unexpandedHeight, unexpandedHeight)

        ta.recycle()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        mHelper.processTouchEvent(event)
        return true
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        parent.requestDisallowInterceptTouchEvent(true)
        // 经过自己的手势判断该次触摸是否该交由helper拦截处理
        if (ev.action == MotionEvent.ACTION_MOVE) {
            val processed = gesture.onTouchEvent(ev)
            if (processed) {
                return mHelper.shouldInterceptTouchEvent(ev)
            }
            return super.onInterceptTouchEvent(ev)
        } else {
            return mHelper.shouldInterceptTouchEvent(ev)
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        rootWidth = measuredWidth
        rootHeight = measuredHeight
    }

    override fun onFinishInflate() {
        super.onFinishInflate()

        val childCount = childCount
        if (childCount < 3) {
            throw IllegalArgumentException("The BrowserHomeLayout childCount less than 3!")
        }

        val children = arrayListOf<View>()
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            when (i) {
                0 -> mHomeHeaderView = child
                1 -> mHomeContentView = child
                2 -> mRightContentView = child
                else -> children.add(child)
            }
        }

        // 移除所有View
        removeAllViews()

        // 按UI层级顺序重新摆放View
        if (mRightContentView != null) {
            addView(mRightContentView)
        }
        if (mHomeContentView != null) {
            addView(mHomeContentView)
        }
        if (mHomeHeaderView != null) {
            addView(mHomeHeaderView)
        }

        for (child in children) {
            addView(child)
        }

    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if (curSnapState != SnapState.SNAP_TO_RIGHT) {
            if (expanded) {
                // 主页顶部
                mHomeHeaderView?.layout(0, 0, mHomeHeaderView!!.measuredWidth, expandedHeight)
                // 主页内容
                mHomeContentView?.layout(0, expandedHeight, mHomeContentView!!.measuredWidth, mHomeContentView!!.measuredHeight + unexpandedHeight)
                endDy = expandedHeight - unexpandedHeight
                endSnapState = SnapState.SNAP_HOME_EXPANDED
            } else {
                // 主页顶部
                mHomeHeaderView?.layout(0, unexpandedHeight - mHomeHeaderView!!.measuredHeight, mHomeHeaderView!!.measuredWidth, unexpandedHeight)
                // 主页内容
                mHomeContentView?.layout(0, unexpandedHeight, mHomeContentView!!.measuredWidth, mHomeContentView!!.measuredHeight + unexpandedHeight)
                endSnapState = SnapState.SNAP_HOME_UNEXPANDED
            }
            // 右侧内容
            mRightContentView?.layout(rootWidth, unexpandedHeight, rootWidth + mRightContentView!!.measuredWidth, mRightContentView!!.measuredHeight + unexpandedHeight)
        } else {
            // 主页顶部
            mHomeHeaderView?.layout(0, unexpandedHeight - mHomeHeaderView!!.measuredHeight, mHomeHeaderView!!.measuredWidth, unexpandedHeight)
            // 主页内容
            mHomeContentView?.layout(-rootWidth, expandedHeight, -rootWidth + mHomeContentView!!.measuredWidth, mHomeContentView!!.measuredHeight + expandedHeight)
            // 右侧内容
            mRightContentView?.layout(0, unexpandedHeight, mRightContentView!!.measuredWidth, mRightContentView!!.measuredHeight + unexpandedHeight)
            endSnapState = SnapState.SNAP_TO_RIGHT
        }
        curSnapState = endSnapState
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        for (i in 0 until childCount) {
            val child = getChildAt(i)
            if (child == mHomeHeaderView) {
                child.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(expandedHeight, MeasureSpec.EXACTLY))
            } else if (child == mHomeContentView || child == mRightContentView) {
                child.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(heightSize - unexpandedHeight, MeasureSpec.EXACTLY))
            } else {
                measureChild(child, widthMeasureSpec, heightMeasureSpec)
            }
        }
        setMeasuredDimension(widthSize, heightSize)
    }

    override fun computeScroll() {
        super.computeScroll()
        if (mHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this@BHLayout)
        } else if (isNeedCallBackForEnd) {
            // 根据标识，此时该回调结束的方法
            curState = endState
            onStateChangeListener?.onStateChange(endView, endState, endSnapState, endDx, endDy)
        }
    }


    /**
     * 获取当前的状态
     *
     * @author Jero
     */
    fun getCurrentState() = curState

    /**
     * 设置拖拽回调
     *
     * @author Jero
     */
    fun setOnDragCallback(onDragBHLayoutCallback: OnDragBHLayoutCallback) {
        this.onDragBHLayoutCallback = onDragBHLayoutCallback
    }

    /**
     * 设置状态变化的监听器对象
     *
     * @param onStateChangeListener 实现了监听器接口 [OnStateChangeListener] 的对象实例
     *
     * @author Jero
     */
    fun setOnStateChangeListener(onStateChangeListener: OnStateChangeListener) {
        this.onStateChangeListener = onStateChangeListener
    }

    /**
     * 页面切换事件的状态，在一次滑动完毕后，会触发此类事件，仅在[STATE_SWITCH_TO_HORIZONTAL]或[STATE_SWITCH_TO_VERTICAL]时有价值，
     * 值分别为：[SNAP_HOME_UNEXPANDED]、[SNAP_HOME_EXPANDED]、[SNAP_TO_RIGHT]、[SNAP_NONE]。
     *
     * 特别注意：[SNAP_NONE] 旨在滑动过程中，用于填充该变量并无变化的值，没有实际意义。
     *
     * @author Jero
     */
    enum class SnapState {
        /**
         * 当前是未展开状态
         *
         * @author Jero
         */
        SNAP_HOME_UNEXPANDED,
        /**
         * 当前是展开状态
         *
         * @author Jero
         */
        SNAP_HOME_EXPANDED,
        /**
         * 当前是切换到右边布局的状态
         *
         * @author Jero
         */
        SNAP_TO_RIGHT,
        /**
         * 当前没有状态发生变化
         *
         * @author Jero
         */
        SNAP_NONE
    }

    /**
     * 滑动的状态变化，具体值有：
     * [STATE_CHANGING_VERTICAL]、[STATE_CHANGING_HORIZONTAL]、
     * [STATE_SWITCH_TO_HORIZONTAL]、[STATE_SWITCH_TO_VERTICAL]、
     * [STATE_IDLE]
     *
     * @author Jero
     */
    enum class State {
        /**
         * 垂直方向上距离变化
         *
         * Created by Jero on 2017/11/22.
         *
         * @author Jero
         */
        STATE_CHANGING_VERTICAL,
        /**
         * 水平方向上的距离变化
         *
         * Created by Jero on 2017/11/22.
         *
         * @author Jero
         */
        STATE_CHANGING_HORIZONTAL,

        /**
         * 水平方向上的状态切换
         *
         * Created by Jero on 2017/11/22.
         *
         * @author Jero
         */
        STATE_SWITCH_TO_HORIZONTAL,

        /**
         *
         * 垂直方向上的状态切换
         *
         * Created by Jero on 2017/11/22.
         *
         * @author Jero
         */
        STATE_SWITCH_TO_VERTICAL,

        /**
         * 当前为空闲状态
         *
         * Created by Jero on 2017/11/22.
         *
         * @author Jero
         */
        STATE_IDLE
    }

    /**
     * 滑动变化的监听器接口，参考回调方法[onStateChange]
     *
     * Created by Jero on 2017/11/22.
     *
     * @author Jero
     */
    interface OnStateChangeListener {
        /**
         *
         * 状态变化的监听器回调方法
         * @param view 正在变化的View
         * @param state 参考 [State]
         * @param snapState 滑动结束时会触发这个事件
         * @param dx 水平方向上的变化距离，当 state == [State.STATE_CHANGING_HORIZONTAL] 时，该变量将表示正在变化的x轴，当手势结束时，该变量也还有值的变化，即为自动执行的补间效果
         * @param dy 垂直方向上的变化距离, 当 state == [State.STATE_CHANGING_VERTICAL] 时，该变量将表示正在变化的y轴，当手势结束时，该变量也还有值的变化，即为自动执行的补间效果
         *
         * Created by Jero on 2017/11/22.
         *
         * @author Jero
         */
        fun onStateChange(view: View?, state: State = State.STATE_IDLE, snapState: SnapState = SnapState.SNAP_NONE, dx: Int = 0, dy: Int = 0)
    }

    /**
     * 拖拽回调，此接口会在滑动事件产生时实时调用
     *
     * @author Jero
     */
    interface OnDragBHLayoutCallback {
        /**
         * 返回此时是否可以执行drag操作
         *
         * @author Jero
         */
        fun onDragBHLayoutEnable(): Boolean = true
    }
}