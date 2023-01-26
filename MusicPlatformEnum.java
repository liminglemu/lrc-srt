package com.teak.core.enums;

/**
 * @author 柚mingle木
 * @version 1.0
 * @date 2023/1/22
 */
public enum MusicPlatformEnum {
    KUWO("酷我", "[kuwo:", 2),
    //    KUGOU("酷狗"),
    TENCENT("企鹅", "[ti:", 5),
    //    WANGYI("网易"),
    GUMI("咕咪", "@migu", 1),
    AIGENERATION("Ai生成", "[by:天琴实验室AI生成", 2);

    private String platform;
    private String matching;
    private int skip;

    public String getPlatform() {
        return platform;
    }

    private MusicPlatformEnum setPlatform(String platform) {
        this.platform = platform;
        return this;
    }

    public String getMatching() {
        return matching;
    }

    private MusicPlatformEnum setMatching(String matching) {
        this.matching = matching;
        return this;
    }

    public int getSkip() {
        return skip;
    }

    private MusicPlatformEnum setSkip(int skip) {
        this.skip = skip;
        return this;
    }

    MusicPlatformEnum(String platform, String matching, int skip) {
        this.platform = platform;
        this.matching = matching;
        this.skip = skip;
    }
}
