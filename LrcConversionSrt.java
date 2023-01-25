package com.teak.core.util;

import com.teak.core.enums.MusicPlatformEnum;
import com.teak.core.function.MyFunction;
import com.teak.core.reportErrors.LrcConversionStrException;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The type Lrc conversion srt.
 *
 * @author 柚mingle木
 * @version 2.0
 * @date 2023 /1/19
 */
public final class LrcConversionSrt {

    private static MusicPlatformEnum musicPlatformEnum;

    private LrcConversionSrt() {
    }

    /**
     * Conversion read array list.<p>
     * 读取文件内容，读取的到的每行数据，读取当前行传入myFunction.action方法,用户自定义返回的数据并返回list<String>数组
     *
     * @param inputPath  the input path
     * @param myFunction the my function
     * @return the array list
     */
    public static @NotNull List<String> conversionRead(String inputPath, MyFunction<String, String> myFunction) {
        List<String> strings = new ArrayList<>();
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(inputPath));
            String source;
            while ((source = bufferedReader.readLine()) != null) {
                String action = myFunction.action(source);
                if (action != null) {
                    strings.add(action);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return strings;
    }

    /**
     * Conversion platform music platform enum.<p>
     * 用于判断歌词平台，因为每个平台的排布都不同，可以跳过不同歌词平台的开头信息，实在麻烦，就不能统一吗
     *
     * @param firstLine the first line
     * @return the music platform enum
     */
    public static MusicPlatformEnum conversionPlatform(@NotNull String firstLine) {
        // TODO: 2023/1/24 这里选择的是startWith(String,1)而不是startWith(String)默认方法是因为在接收函数接口中的String字符串
        //  时，在转换成byte数组时多出了
        //        bytes = -17
        //        bytes = -69
        //        bytes = -65
        //        这三个bytes数字使用new String(byte[])方法输出后就得到一个空格，虽然我有试过使用String.trim方法进行删除空格，但是
        //        好像不行（有可能根本不是空格，而是无法显示的字符，所以trim无法生效），索性就不用了，直接在选择匹配偏移度
        if (firstLine.startsWith("[ti", 1)) {
            return MusicPlatformEnum.TENCENT;
        }
        if (firstLine.startsWith("[kuwo", 1)) {
            return MusicPlatformEnum.KUWO;
        }
        if (firstLine.startsWith("[by:天琴实验室AI生成", 1)) {
            return MusicPlatformEnum.AIGENERATION;
        }
        if (firstLine.startsWith("@migu", 1)) {
            return MusicPlatformEnum.GUMI;
        }
        throw new LrcConversionStrException("无法识别歌词平台，请手动进行修改后重试");
    }

    /**
     * Conversion format data string.<p>
     * 复制前的前10个拷贝到bytes[]中
     *
     * @param source the source
     * @return the string
     */
    public static @NotNull String conversionFormatData(@NotNull String source) {
        byte[] bytes = new byte[10];
        System.arraycopy(source.getBytes(StandardCharsets.UTF_8), 0, bytes, 0, 10);
        return getString(bytes);
    }

    private static @NotNull String getString(byte[] bytes) {
        String target = new String(bytes, StandardCharsets.UTF_8);
        String replace1 = target.replace('[', ' ');
        String replace2 = replace1.replace(']', ' ');
        String replace3 = replace2.replace('.', ',');
        return "00:" + replace3.trim() + "9";
    }

    /**
     * Conversion format data ku wo string.<p>
     * 正对酷我格式进行转换，但是我发现酷我不止有一种格式，所以后面还要更新
     *
     * @param source the source
     * @return the string
     */
    // TODO: 2023/1/25 需要更新，暂时仅匹配部分格式
    public static @NotNull String conversionFormatDataKuWo(@NotNull String source) {
        byte[] bytes = new byte[11];
        System.arraycopy(source.getBytes(StandardCharsets.UTF_8), 0, bytes, 0, 11);
        String target = new String(bytes, StandardCharsets.UTF_8);
        String replace1 = target.replace('[', ' ');
        String replace2 = replace1.replace(']', ' ');
        String replace3 = replace2.replace('.', ',');
        return "00:" + replace3.trim();
    }

    /**
     * Conversion sub string string.<p>
     * 截取字符串时间戳后的歌词，如果时间戳后的歌词是空，那么返回null
     *
     * @param source the source
     * @return the string
     */
    @Contract(pure = true)
    public static @NotNull String conversionSubString(@NotNull String source) {
        String subString = source.substring(10);
        if (!subString.equals("")) {
            return subString;
        }
        return "null";
    }

    /**
     * Conversion sub string ku wo string.<p>
     * 正对酷我截取的歌词，但是还是无法匹配所有格式，后期更新
     *
     * @param source the source
     * @return the string
     */
    // TODO: 2023/1/25 后期更新
    public static @NotNull String conversionSubStringKuWo(@NotNull String source) {
        return source.substring(11);
    }

    /**
     * Process the last timestamp string.<p>
     * 将最后一行的时间戳传入，将最后一行时间戳时间加5然后返回，因为不会使用date时间工具，索性使用字符串了
     *
     * @param lastTimestamp the last timestamp
     * @return the string
     */
    public static @Nullable String processTheLastTimestamp(@NotNull String lastTimestamp) {
        System.out.println("lastTimestamp = " + lastTimestamp);
        String[] split = lastTimestamp.split("[:,]");

        int hourInt = Integer.parseInt(split[0]);
        int minuteInt = Integer.parseInt(split[1]);
        int secondInt = Integer.parseInt(split[2]) + 7;
        int millisecondInt = Integer.parseInt(split[3]);

        if (secondInt >= 60) {
            minuteInt = minuteInt + 1;
            secondInt = secondInt - 60;
        }

        List<Integer> integers = new ArrayList<>(3);
        integers.add(hourInt);
        integers.add(minuteInt);
        integers.add(secondInt);

        List<String> lastTimestampList = integers.stream().map(date -> {
            if (date == 0) {
                return "00";
            }
            if ((date / 10) < 1) {
                return "0" + date;
            }
            return String.valueOf(date);
        }).collect(Collectors.toList());

        return lastTimestampList.get(0) + ":" + lastTimestampList.get(1) + ":" + lastTimestampList.get(2) + "," + millisecondInt;
    }


    /**
     * Conversion action automatic detection.<p>
     * 此方法为改进版本，用户不用提供平台名字，自动判断，自动跳过开头，自动匹配不同长度时间戳
     * 暂时无法匹配部分酷我歌词，因为有些例外存在，比较好的匹配就是企鹅平台歌词
     *
     * @param inputPath  the input path
     * @param outputPath the output path
     */
    public static void conversionActionAutomaticDetection(String inputPath, String outputPath) {
        List<String> dateCollect;
        List<String> lyricCollect;
        List<String> sourceList = LrcConversionSrt.conversionRead(inputPath, new MyFunction<>() {

            private int i = 1;
            private int skip = 0;


            @Override
            public String action(String source) {
                if (i++ == 1) {
                    musicPlatformEnum = conversionPlatform(source);
                    System.out.println("平台 = " + musicPlatformEnum.getPlatform());
                    System.out.println("匹配字符 = " + musicPlatformEnum.getMatching());
                    skip = musicPlatformEnum.getSkip();
                }
                if (i - 1 > skip) {
                    return source;
                }
                return null;
            }
        });
        for (String string : sourceList) {
            System.out.println("string = " + string);
        }
        if (musicPlatformEnum.getPlatform().equals("酷我") || musicPlatformEnum.getPlatform().equals("Ai生成")) {
            dateCollect = sourceList.stream().map(LrcConversionSrt::conversionFormatDataKuWo).collect(Collectors.toList());
            lyricCollect = sourceList.stream().map(LrcConversionSrt::conversionSubStringKuWo).collect(Collectors.toList());
        } else {
            dateCollect = sourceList.stream().map(LrcConversionSrt::conversionFormatData).collect(Collectors.toList());
            lyricCollect = sourceList.stream().map(LrcConversionSrt::conversionSubString).collect(Collectors.toList());
        }
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(outputPath));
            int temp;
            for (int i = 0; i < dateCollect.size(); i++) {
                temp = i + 1;
                String currentTimestamp = dateCollect.get(i);
                String lyric = lyricCollect.get(i);
                String nextTimestamp;
                if ("null".equals(lyric)) {
                    continue;
                }
                if (temp != dateCollect.size()) {
                    nextTimestamp = dateCollect.get(temp);
                } else {
                    nextTimestamp = processTheLastTimestamp(currentTimestamp);
                }
                bufferedWriter.write(i + 1 + "\n" + currentTimestamp + " --> " + nextTimestamp + "\n" + lyric + "\n" + "\n");
                bufferedWriter.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // TODO: 2023/1/19 [[\s\S]*?]正则表达式在网页上可以识别[00:02.40]标准字样,这里好像不太行
}
