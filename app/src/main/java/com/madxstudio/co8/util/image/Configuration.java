/*
 *
 *             $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
 *             $                                                   $
 *             $                       _oo0oo_                     $
 *             $                      o8888888o                    $
 *             $                      88" . "88                    $
 *             $                      (| -_- |)                    $
 *             $                      0\  =  /0                    $
 *             $                    ___/`-_-'\___                  $
 *             $                  .' \\|     |$ '.                 $
 *             $                 / \\|||  :  |||$ \                $
 *             $                / _||||| -:- |||||- \              $
 *             $               |   | \\\  -  $/ |   |              $
 *             $               | \_|  ''\- -/''  |_/ |             $
 *             $               \  .-\__  '-'  ___/-. /             $
 *             $             ___'. .'  /-_._-\  `. .'___           $
 *             $          ."" '<  `.___\_<|>_/___.' >' "".         $
 *             $         | | :  `- \`.;`\ _ /`;.`/ - ` : | |       $
 *             $         \  \ `_.   \_ __\ /__ _/   .-` /  /       $
 *             $     =====`-.____`.___ \_____/___.-`___.-'=====    $
 *             $                       `=-_-='                     $
 *             $     ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~   $
 *             $                                                   $
 *             $          Buddha Bless         Never Bug           $
 *             $                                                   $
 *             $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
 */

/*
 *
 *             $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
 *             $                                                   $
 *             $                       _oo0oo_                     $
 *             $                      o8888888o                    $
 *             $                      88" . "88                    $
 *             $                      (| -_- |)                    $
 *             $                      0\  =  /0                    $
 *             $                    ___/`-_-'\___                  $
 *             $                  .' \\|     |$ '.                 $
 *             $                 / \\|||  :  |||$ \                $
 *             $                / _||||| -:- |||||- \              $
 *             $               |   | \\\  -  $/ |   |              $
 *             $               | \_|  ''\- -/''  |_/ |             $
 *             $               \  .-\__  '-'  ___/-. /             $
 *             $             ___'. .'  /-_._-\  `. .'___           $
 *             $          ."" '<  `.___\_<|>_/___.' >' "".         $
 *             $         | | :  `- \`.;`\ _ /`;.`/ - ` : | |       $
 *             $         \  \ `_.   \_ __\ /__ _/   .-` /  /       $
 *             $     =====`-.____`.___ \_____/___.-`___.-'=====    $
 *             $                       `=-_-='                     $
 *             $     ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~   $
 *             $                                                   $
 *             $          Buddha bless         Never BUG           $
 *             $                                                   $
 *             $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
 */

package com.madxstudio.co8.util.image;

import android.net.Uri;
import android.support.annotation.DrawableRes;
import android.support.annotation.IntDef;

import com.madxstudio.co8.R;

import java.io.File;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 加载图片的参数配置类，这里可以使用{@link Uri}, {@link String}, {@link File}, {@link byte[]},
 * {@link DrawableRes}等类型来指定图片。使用其中任何一中类型即可，切不可同时指定多种类型同一使用
 * Created 16/7/24.
 *
 * @author Jackie
 * @version 1.1
 */
public class Configuration {

    public static final int CENTER_CROP = 0x3e8;
    public static final int FIT_CENTER = 0x7d0;
    public static final int NORMAL = 0xbb8;

    /**
     * 缓存数据源，网络图片为节省流量，请至少要缓存数据源
     */
    public static final int CACHE_STRATEGY_SOURCE = 0b001;

    /**
     * 缓存加载后的数据，这种加载方式推荐使用在加载本地图片时只需要缓存加载后的数据，源本已在本地再做缓存已是无端占用系
     * 统空间。所以加载本地数据时不推荐使用
     */
    public static final int CACHE_STRATEGY_RESULT = 0b010;

    /**
     * 缓存源和加载后的数据，这个是目前默认的加载方式
     */
    public static final int CACHE_STRATEGY_ALL = 0b11;

    /**
     * 指定图片的{@link Uri}, 使用了这一种类型来加载本图片
     */
    public Uri uri = null;

    /**
     * 指定图片的{@link String 路径}, 使用了这一种类型来加载本图片
     */
    public String url = null;

    /**
     * 指定图片的{@link File}, 使用了这一种类型来加载本图片
     */
    public File file = null;

    /**
     * 指定图片的{@link byte[]}数组, 使用了这一种类型来加载本图片
     */
    public byte[] model = null;

    /**
     * 指定图片的{@link DrawableRes}, 使用了这一种类型来加载本图片
     */
    @DrawableRes
    public Integer drawableId;

    /**
     * 指定加载图片的宽度
     */
    public int width = ImageLoadUtil.SIZE_ORIGINAL;

    /**
     * 指定加载图片的高度
     */
    public int height = ImageLoadUtil.SIZE_ORIGINAL;

    /**
     * 加载图片出错时显示的图片资源Id
     */
    @DrawableRes
    public int errorDrawableId = R.drawable.network_image_default;
    /**
     * 图片还未加载时显示的图片资源Id
     */
    @DrawableRes
    public int placeholderId = R.drawable.network_image_default;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({CENTER_CROP, FIT_CENTER, NORMAL})
    public @interface ScaleType {
    }

    /**
     * 图片加载的方式
     */
    @ScaleType
    public int scaleType = NORMAL;

    /**
     * 是否不执行动画
     */
    public boolean doNotAnimate = false;

    /**
     * 模糊渐进效果
     */
    public boolean thumbnailAvailable = false;

    /**
     * 是否是gif图片
     */
    public boolean isGif = false;

    /**
     * 小图上的{@link Uri}, 功能用于加载大图时可以预先加载小图显示
     */
    public Uri thumbnailUri;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({CACHE_STRATEGY_ALL, CACHE_STRATEGY_RESULT, CACHE_STRATEGY_SOURCE})
    public @interface CacheStrategy {
    }

    /**
     * 加载图片的缓存方式
     *
     * @see #CACHE_STRATEGY_SOURCE
     * @see #CACHE_STRATEGY_RESULT
     * @see #CACHE_STRATEGY_ALL
     */
    @CacheStrategy
    public int cacheStrategy = CACHE_STRATEGY_ALL;

    public Configuration() {
    }
}
