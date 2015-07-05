package com.intimation.demoquiz.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.utils.DiscCacheUtils;
import com.nostra13.universalimageloader.utils.MemoryCacheUtils;

import java.io.File;

public class ImageLoaderUtil {

	private static ImageLoader imageLoader;
	private static DisplayImageOptions options;

	public static void ConfigureImageLoader(Context context) {
		if (imageLoader==null) {
			imageLoader = ImageLoader.getInstance();

			ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
					.memoryCache(new WeakMemoryCache())
					.memoryCacheSize(2 * 1024 * 1024)
					.denyCacheImageMultipleSizesInMemory()
					.discCache(new UnlimitedDiscCache(
							context.getExternalFilesDir(null) != null
									? context.getExternalFilesDir(null) : context.getCacheDir()))
					.threadPoolSize(5)
					.build();

			imageLoader.init(config);

			options = new DisplayImageOptions.Builder()
					.imageScaleType(ImageScaleType.IN_SAMPLE_INT)
					.displayer(new FadeInBitmapDisplayer(300))
					.bitmapConfig(Bitmap.Config.RGB_565)
					.resetViewBeforeLoading(true)
					.considerExifParams(true)
					.cacheOnDisc(true)
					.build();
		}
	}


	private ImageLoaderUtil() {}

	public static File getFile(String url) {
		return imageLoader.getDiscCache().get(url);
	}

	public static void displayImage(final ImageView imageView, final ProgressBar progressBar, final String imageUrl) {
		if (imageUrl == null || imageUrl.trim().length() == 0 || !imageUrl.startsWith("http")) {
			return;
		}

		imageLoader.displayImage(imageUrl, imageView, options, new SimpleImageLoadingListener() {
			@Override
			public void onLoadingStarted(String imageUri, View view) {
				if (progressBar != null) {
					progressBar.setProgress(0);
					progressBar.setVisibility(View.VISIBLE);
				}
			}

			@Override
			public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
				if (progressBar != null) {
					progressBar.setVisibility(View.GONE);
				}
			}

			@Override
			public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
				if (progressBar != null) {
					progressBar.setVisibility(View.GONE);
				}
			}
		}, new ImageLoadingProgressListener() {
			@Override
			public void onProgressUpdate(String imageUri, View view, int current, int total) {
				if (progressBar != null) {
					progressBar.setProgress(Math.round(100.0f * current / total));
				}
			}
		});
	}

	public static void displayImage(final ImageView imageView, final ProgressBar progressBar, final String imageUrl, SimpleImageLoadingListener listener) {
		if (imageUrl == null || imageUrl.trim().length() == 0 || !imageUrl.startsWith("http")) {
			return;
		}

		imageLoader.displayImage(imageUrl, imageView, options, listener, new ImageLoadingProgressListener() {
			@Override
			public void onProgressUpdate(String imageUri, View view, int current, int total) {
				if (progressBar != null) {
					progressBar.setProgress(Math.round(100.0f * current / total));
				}
			}
		});
	}

	public static void displayImage(ImageView imageView, final ProgressBar progressBar, String imageUrl, boolean clearCache) {
		MemoryCacheUtils.removeFromCache(imageUrl, imageLoader.getMemoryCache());
		DiscCacheUtils.removeFromCache(imageUrl, imageLoader.getDiscCache());

		displayImage(imageView, progressBar, imageUrl);
	}

	public static void displayImage(ImageView imageView, String imageUrl) {
		imageLoader.displayImage(imageUrl, imageView);
	}

	public static void loadImage(String imageUrl) {
		imageLoader.loadImage(imageUrl, options, null);
	}

	public static void loadImage(String imageUrl, ImageLoadingListener listener) {
		imageLoader.loadImage(imageUrl, options, listener);
	}

	public static void clearCache(String image_url) {
		MemoryCacheUtils.removeFromCache(image_url, imageLoader.getMemoryCache());
		DiscCacheUtils.removeFromCache(image_url, imageLoader.getDiscCache());
	}

	public static void clearAllCache() {
		if (imageLoader != null) {
			imageLoader.clearMemoryCache();
			imageLoader.clearDiscCache();
		}
	}
}
