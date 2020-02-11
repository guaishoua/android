package com.android.tacu.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import com.android.tacu.R;
import com.android.tacu.utils.UIUtils;

import java.util.ArrayList;
import java.util.List;

public class NodeProgressView extends View {

    //node 默认
    private Paint mNodePaint;
    //node 已完成
    private Paint mNodeProgressPaint;

    private Paint mLinePaint;
    private Paint mLineProgressPaint;

    private Paint mTextPaint;
    private Paint mTextProgressPaint;

    private int mNodeColor;
    private int mNodeProgressColor;

    private int mTextColor;
    private int mTextProgressColor;

    private int mLineColor;
    private int mLineProgressColor;

    //node 半径
    private int mNodeRadio;
    //节点个数
    private int mNumber;
    private List<Node> nodes = new ArrayList<>();
    private List<String> nodeTexts = new ArrayList<>();
    private int mCurentNode;

    private int mWidth;
    private int mHeight;

    private int leftM = UIUtils.dp2px(30);
    private int rightM = UIUtils.dp2px(30);

    public NodeProgressView(Context context) {
        this(context, null);
    }

    public NodeProgressView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NodeProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(attrs);
        init();
    }

    private void initAttrs(AttributeSet attrs) {
        TypedArray mTypedArray = getContext().obtainStyledAttributes(attrs, R.styleable.nodeProgresssView);
        mNodeColor = mTypedArray.getColor(R.styleable.nodeProgresssView_node_color, Color.GRAY);
        mNodeProgressColor = mTypedArray.getColor(R.styleable.nodeProgresssView_node_progresscolor, Color.RED);

        mTextColor = mTypedArray.getColor(R.styleable.nodeProgresssView_node_tip, Color.GRAY);
        mTextProgressColor = mTypedArray.getColor(R.styleable.nodeProgresssView_node_progress_tip, Color.RED);

        mLineColor = mTypedArray.getColor(R.styleable.nodeProgresssView_node_bar, Color.GRAY);
        mLineProgressColor = mTypedArray.getColor(R.styleable.nodeProgresssView_node_progress_bar, Color.RED);

        mNumber = mTypedArray.getInt(R.styleable.nodeProgresssView_node_num, 2);
        mCurentNode = mTypedArray.getInt(R.styleable.nodeProgresssView_node_current, 0);
        mNodeRadio = mTypedArray.getDimensionPixelSize(R.styleable.nodeProgresssView_node_radio, 10);
    }

    private void init() {
        nodeTexts.add(getResources().getString(R.string.market_order));
        nodeTexts.add(getResources().getString(R.string.buyer_pay));
        nodeTexts.add(getResources().getString(R.string.seller_coin));
        nodeTexts.add(getResources().getString(R.string.order_finish));

        mNodePaint = new Paint();
        mNodePaint.setAntiAlias(true);
        mNodePaint.setStyle(Paint.Style.FILL);
        mNodePaint.setColor(mNodeColor);

        mNodeProgressPaint = new Paint();
        mNodeProgressPaint.setAntiAlias(true);
        mNodeProgressPaint.setStyle(Paint.Style.FILL);
        mNodeProgressPaint.setColor(mNodeProgressColor);

        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setColor(mTextColor);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setTextSize(UIUtils.dp2px(12));

        mTextProgressPaint = new Paint();
        mTextProgressPaint.setAntiAlias(true);
        mTextProgressPaint.setStyle(Paint.Style.FILL);
        mTextProgressPaint.setColor(mTextProgressColor);
        mTextProgressPaint.setTextAlign(Paint.Align.CENTER);
        mTextProgressPaint.setTextSize(UIUtils.dp2px(12));

        mLinePaint = new Paint();
        mLinePaint.setAntiAlias(true);
        mLinePaint.setStrokeWidth(UIUtils.dp2px(3));
        mLinePaint.setStyle(Paint.Style.FILL);
        mLinePaint.setColor(mLineColor);

        mLineProgressPaint = new Paint();
        mLineProgressPaint.setAntiAlias(true);
        mLineProgressPaint.setStrokeWidth(UIUtils.dp2px(3));
        mLineProgressPaint.setStyle(Paint.Style.FILL);
        mLineProgressPaint.setColor(mLineProgressColor);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        mWidth = getMeasuredWidth() - leftM - rightM;
        mHeight = getMeasuredHeight();
        int d = (mWidth - getPaddingLeft() - getPaddingRight()) / (mNumber - 1);

        nodes = new ArrayList<>();
        for (int i = 0; i < mNumber; i++) {
            if (i == 0) {
                Node node = new Node();
                Point paint = new Point(getPaddingLeft() + mNodeRadio + leftM, mHeight / 2);
                node.setPoint(paint);
                if (nodeTexts != null && nodeTexts.size() > 0 && !TextUtils.isEmpty(nodeTexts.get(i))) {
                    node.setDes(nodeTexts.get(i));
                } else {
                    node.setDes(String.valueOf(i + 1));
                }
                nodes.add(node);
                continue;
            }

            if (i == mNumber - 1) {
                Node node = new Node();
                Point paint = new Point(mWidth - getPaddingRight() - mNodeRadio + leftM, mHeight / 2);
                node.setPoint(paint);
                if (nodeTexts != null && nodeTexts.size() > 0 && !TextUtils.isEmpty(nodeTexts.get(i))) {
                    node.setDes(nodeTexts.get(i));
                } else {
                    node.setDes(String.valueOf(i + 1));
                }
                nodes.add(node);
                continue;
            }
            Node node = new Node();
            Point paint = new Point(getPaddingLeft() + d * i - mNodeRadio + leftM, mHeight / 2);
            node.setPoint(paint);
            if (nodeTexts != null && nodeTexts.size() > 0 && !TextUtils.isEmpty(nodeTexts.get(i))) {
                node.setDes(nodeTexts.get(i));
            } else {
                node.setDes(String.valueOf(i + 1));
            }
            nodes.add(node);
        }
    }

    private Canvas mCanvas;

    @Override
    protected void onDraw(Canvas canvas) {
        mCanvas = canvas;
        drawLineProgress(canvas);
        drawNodeProgress(canvas);
        drawTextProgress(canvas);
    }

    public void clear() {
        Paint paint = new Paint();
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        mCanvas.drawPaint(paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
    }


    private void drawLineProgress(Canvas canvas) {
        int startX = getPaddingLeft() + mNodeRadio * 2 + leftM;
        for (int i = 1; i < nodes.size(); i++) {
            Node node = nodes.get(i);
            Point point = node.getPoint();
            int x = point.x;
            int y = point.y;
            x = x - mNodeRadio;
            if (mCurentNode >= i) {
                canvas.drawLine(startX, y, x, y, mLineProgressPaint);
            } else {
                canvas.drawLine(startX, y, x, y, mLinePaint);
            }
            startX = x + mNodeRadio * 2;
        }
    }

    private void drawNodeProgress(Canvas canvas) {
        for (int i = 0; i < nodes.size(); i++) {
            Node node = nodes.get(i);
            Point point = node.getPoint();
            if (i < mCurentNode) {
                canvas.drawCircle(point.x, point.y, mNodeRadio, mNodeProgressPaint);
            } else if ((i != 0) && (i == mCurentNode) && (mCurentNode == nodes.size())) {
                canvas.drawCircle(point.x, point.y, mNodeRadio, mNodeProgressPaint);
            } else {
                canvas.drawCircle(point.x, point.y, mNodeRadio, mNodePaint);
            }
        }
    }

    private void drawTextProgress(Canvas canvas) {
        for (int i = 0; i < nodes.size(); i++) {
            Node node = nodes.get(i);
            Point point = node.getPoint();
            String text = node.getDes();
            Paint.FontMetricsInt fmi = mTextPaint.getFontMetricsInt();

            if (i < mCurentNode) {
                canvas.drawText(text, point.x, point.y + mNodeRadio + fmi.bottom - fmi.top, mTextProgressPaint);
            } else if ((i != 0) && (i == mCurentNode) && (mCurentNode == nodes.size())) {
                canvas.drawText(text, point.x, point.y + mNodeRadio + fmi.bottom - fmi.top, mTextProgressPaint);
            } else {
                canvas.drawText(text, point.x, point.y + mNodeRadio + fmi.bottom - fmi.top, mTextPaint);
            }
        }
    }

    public void setmNumber(int mNumber) {
        this.mNumber = mNumber;
    }

    public void setNodeTexts(List<String> texts) {
        this.nodeTexts = texts;
    }

    public void setCurentNode(int curentNode) {
        this.mCurentNode = curentNode - 1;
    }

    public void reDraw() {
        clear();
        invalidate();
    }

    class Node {
        private Point point;
        private String des;

        public Point getPoint() {
            return point;
        }

        public void setPoint(Point point) {
            this.point = point;
        }

        public String getDes() {
            return des;
        }

        public void setDes(String des) {
            this.des = des;
        }
    }
}
