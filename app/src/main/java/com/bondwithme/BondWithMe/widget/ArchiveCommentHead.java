package com.bondwithme.BondWithMe.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.entity.ArchiveChatEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * A RecyclerView.Adapter that allows for headers and footers as well.
 *
 * This class wraps a base adapter that's passed into the constructor. It works by creating extra view items types
 * that are returned in {@link #getItemViewType(int)}, and mapping these to the header and footer views provided via
 * {@link #addHeader(View)} and {@link #addFooter(View)}.
 *
 * There are two restrictions when using this class:
 *
 * 1) The base adapter can't use negative view types, since this class uses negative view types to keep track
 *    of header and footer views.
 *
 * 2) You can't add more than 1000 headers or footers.
 *
 * Created by mlapadula on 12/15/14.
 */
public class ArchiveCommentHead<T extends RecyclerView.Adapter> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

	private final T mBase;
	private Context mContext;
	private List<ArchiveChatEntity> data;
	private ArchiveChatEntity archive;


	/**
	 * Defines available view type integers for headers and footers.
	 *
	 * How this works:
	 * - Regular views use view types starting from 0, counting upwards
	 * - Header views use view types starting from -1000, counting upwards
	 * - Footer views use view types starting from -2000, counting upwards
	 *
	 * This means that you're safe as long as the base adapter doesn't use negative view types,
	 * and as long as you have fewer than 1000 headers and footers
	 */
	private static final int HEADER_VIEW_TYPE = -1000;
	private static final int FOOTER_VIEW_TYPE = -2000;

	private final List<View> mHeaders = new ArrayList<View>();
	private final List<View> mFooters = new ArrayList<View>();

	/**
	 * Constructor.
	 *
	 * @param base
	 * 		the adapter to wrap
	 */
	public ArchiveCommentHead(T base,Context context ,List<ArchiveChatEntity> data) {
		super();
		mContext = context;
		this.data = data;
		mBase = base;
	}

	/**
	 * Gets the base adapter that this is wrapping.
	 */
	public T getWrappedAdapter() {
		return mBase;
	}

	/**
	 * Adds a header view.
	 */
	public void addHeader(@NonNull View view) {
		if (view == null) {
			throw new IllegalArgumentException("You can't have a null header!");
		}
		mHeaders.add(view);
	}

	/**
	 * Adds a footer view.
	 */
	public void addFooter(@NonNull View view) {
		if (view == null) {
			throw new IllegalArgumentException("You can't have a null footer!");
		}
		mFooters.add(view);
	}

	/**
	 * Toggles the visibility of the header views.
	 */
	public void setHeaderVisibility(boolean shouldShow) {
		for (View header : mHeaders) {
			header.setVisibility(shouldShow ? View.VISIBLE : View.GONE);
		}
	}

	/**
	 * Toggles the visibility of the footer views.
	 */
	public void setFooterVisibility(boolean shouldShow) {
		for (View footer : mFooters) {
			footer.setVisibility(shouldShow ? View.VISIBLE : View.GONE);
		}
	}

	/**
	 * @return the number of headers.
	 */
	public int getHeaderCount() {
		return mHeaders.size();
	}

	/**
	 * @return the number of footers.
	 */
	public int getFooterCount() {
		return mFooters.size();
	}

	/**
	 * Gets the indicated header, or null if it doesn't exist.
	 */
	public View getHeader(int i) {
		return i < mHeaders.size() ? mHeaders.get(i) : null;
	}

	/**
	 * Gets the indicated footer, or null if it doesn't exist.
	 */
	public View getFooter(int i) {
		return i < mFooters.size() ? mFooters.get(i) : null;
	}

	private boolean isHeader(int viewType) {
		return viewType >= HEADER_VIEW_TYPE && viewType < (HEADER_VIEW_TYPE + mHeaders.size());
	}

	private boolean isFooter(int viewType) {
		return viewType >= FOOTER_VIEW_TYPE && viewType < (FOOTER_VIEW_TYPE + mFooters.size());
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
		if (isHeader(viewType)) {
			int whichHeader = Math.abs(viewType - HEADER_VIEW_TYPE);
			View headerView = mHeaders.get(whichHeader);
			return new VHHeader(headerView);
		} else if (isFooter(viewType)) {
			int whichFooter = Math.abs(viewType - FOOTER_VIEW_TYPE);
			View footerView = mFooters.get(whichFooter);
			return new VHHeader(footerView);

		} else {
			return mBase.onCreateViewHolder(viewGroup, viewType);
		}
	}

	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
		if (position < mHeaders.size()) {
			archive = data.get(position);
			VHHeader item = (VHHeader) holder;
			item.tvUserName.setText("111111");

		}else if (position < mHeaders.size() + mBase.getItemCount()) {
			// This is a real position, not a header or footer. Bind it.
			mBase.onBindViewHolder(holder, position - mHeaders.size());

		} else {
			// Footers don't need anything special
		}
	}


//	public void onBindViewHolder(ArchiveCommentHead.VHHeader holder, int position) {
//		if (position < mHeaders.size()) {
//
//			// Headers don't need anything special
//			final ArchiveChatEntity  archive = data.get(position);
////			if(TextUtils.isEmpty(archive.getFile_id())){
////			viewHolder.llArchiveImage.setVisibility(View.GONE);
////			}else {
////				//如果有照片
////				holder.llArchiveImage.setVisibility(View.VISIBLE);
////				//照片的总数
////				int PhotoCount = Integer.valueOf(archive.getPhoto_count());
////				if(PhotoCount > 1){
////					String photoCountStr;
////					photoCountStr = PhotoCount + " " + mContext.getString(R.string.text_photos);
////					holder.tvPhotoCount.setText(photoCountStr);
////					holder.tvPhotoCount.setVisibility(View.VISIBLE);
////				}else {
////					holder.tvPhotoCount.setVisibility(View.GONE);
////				}
////				//加载照片
////				BitmapTools.getInstance(mContext).display( holder.imArchiveImages, String.format(Constant.API_GET_PIC, Constant.Module_preview, archive.getUser_id(), archive.getFile_id()), R.drawable.network_image_default, R.drawable.network_image_default);
////			}
//			holder.tvUserName.setText(archive.getGroup_name());
////
////			String Content = archive.getText_description();
////			if(TextUtils.isEmpty(Content)){
//////            holder.liContent.setVisibility(View.VISIBLE);
////				holder.liContent.setVisibility(View.GONE);
////			}else {
////				holder.tvContent.setText(Content);
////				holder.liContent.setVisibility(View.VISIBLE);
////			}
////			holder.tvLoveCount.setText(archive.getLove_count());
////			holder.tvCommentCount.setText(archive.getComment_count());
//////        String tempdate = MyDateUtils.getLocalDateStringFromUTC(mContext, archive.getContent_creation_date());
////			holder.tvDate.setText(MyDateUtils.getLocalDateStringFromUTC(mContext, archive.getContent_creation_date()));
//////        holder.tvDate.setText(tempdate);
//
//		} else if (position < mHeaders.size() + mBase.getItemCount()) {
//			// This is a real position, not a header or footer. Bind it.
//			mBase.onBindViewHolder(holder, position - mHeaders.size());
//
//		} else {
//			// Footers don't need anything special
//		}
//	}

	@Override
	public int getItemCount() {
		return mHeaders.size() + mBase.getItemCount() + mFooters.size();
	}

	@Override
	public int getItemViewType(int position) {
		if (position < mHeaders.size()) {
			return HEADER_VIEW_TYPE + position;

		} else if (position < (mHeaders.size() + mBase.getItemCount())) {
			return mBase.getItemViewType(position - mHeaders.size());

		} else {
			return FOOTER_VIEW_TYPE + position - mHeaders.size() - mBase.getItemCount();
		}
	}

	class VHHeader extends RecyclerView.ViewHolder implements View.OnClickListener {

		/**
		 * 用户昵称视图
		 */
		TextView tvUserName;
		/**
		 * 用于显示图片总数的提示控件
		 */
		TextView tvPhotoCount;
		/**
		 * 发表日期显示视图
		 */
		TextView tvDate;
		/**
		 * 评论总数视图
		 */
		TextView tvCommentCount;
		/**
		 * 喜欢的总数视图
		 */
		TextView tvLoveCount;
		/**
		 * 内容视图
		 */
		TextView tvContent;

		View liContent;

		View llArchiveImage;

		/**
		 * 显示网络图片的视图控件
		 */
		NetworkImageView imArchiveImages;

		public VHHeader(View itemView) {
			super(itemView);
			tvUserName = (TextView) itemView.findViewById(R.id.owner_name);
			tvDate = (TextView) itemView.findViewById(R.id.up_time);
			tvPhotoCount = (TextView) itemView.findViewById(R.id.tv_chat_photo_count);
			llArchiveImage = itemView.findViewById(R.id.ll_chats_image);
			tvCommentCount = (TextView) itemView.findViewById(R.id.member_comment);
			tvLoveCount = (TextView) itemView.findViewById(R.id.memeber_love);
			liContent = itemView.findViewById(R.id.li_content);
			tvContent = (TextView) itemView.findViewById(R.id.tv_archive_content);
			tvContent.setMaxLines(9);
			imArchiveImages = (NetworkImageView) itemView.findViewById(R.id.iv_chats_images);
			tvContent.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
				@Override
				public void onGlobalLayout() {
					// 字符显示超过9行，只显示到第九行
					int lineCount = tvContent.getLineCount();
					if(lineCount > 8) {
						// 第九行只显示十个字符
						int maxLineEndIndex = tvContent.getLayout().getLineEnd(8);
						int maxLineStartIndex = tvContent.getLayout().getLineStart(8);
						String sourceText = tvContent.getText().toString();
						if(maxLineEndIndex - maxLineStartIndex > 7) {
							// 截取到第九行文字的第7个字符，其余字符用...替代
							String newText = sourceText.substring(0, maxLineStartIndex + 7) + "...";
							tvContent.setText(newText);
						} else if(lineCount > 9) {
							// 截取到第九行文字未满7个字符，行数超过9号，说明有换行，将换行替换成"..."
							String newText = sourceText.substring(0, maxLineEndIndex - 1) + "...";
							tvContent.setText(newText);
						}

					}
				}
			});

			imArchiveImages.setOnClickListener(this);

//			tvUserName.setText(archive.getGroup_name());
		}

		@Override
		public void onClick(View v) {

		}
	}
}
