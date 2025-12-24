package com.tzc.driveapp.model;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Question {
    @SerializedName("questionId")
    private Long questionId;

    @SerializedName("questionNo")
    private Long questionNo;

    @SerializedName("type")
    private String type;

    @SerializedName("content")
    private String content;

    @SerializedName("chapter")
    private String chapter;

    @SerializedName("option")
    private String option; // JSON格式的选项字符串

    @SerializedName("difficuity")
    private String difficuity;

    @SerializedName("explain")
    private String explain;

    @SerializedName("img")
    private String img;

    @SerializedName("subject")
    private String subject;

    // 解析后的选项对象
    private Options optionsObj;

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public Long getQuestionNo() {
        return questionNo;
    }

    public void setQuestionNo(Long questionNo) {
        this.questionNo = questionNo;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getChapter() {
        return chapter;
    }

    public void setChapter(String chapter) {
        this.chapter = chapter;
    }

    public String getOption() {
        return option;
    }

    public void setOption(String option) {
        this.option = option;
        // 解析选项
        parseOptions();
    }

    public String getDifficuity() {
        return difficuity;
    }

    public void setDifficuity(String difficuity) {
        this.difficuity = difficuity;
    }

    public String getExplain() {
        return explain;
    }

    public void setExplain(String explain) {
        this.explain = explain;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    // 解析选项JSON
    private void parseOptions() {
        if (option != null && !option.isEmpty()) {
            try {
                // 去除可能的转义字符
                String jsonStr = option.replace("\\\"", "\"");
                optionsObj = Options.fromJson(jsonStr);
            } catch (Exception e) {
                e.printStackTrace();
                optionsObj = new Options();
            }
        }
    }

    public Options getOptionsObj() {
        if (optionsObj == null) {
            parseOptions();
        }
        return optionsObj;
    }

    // 选项内部类
    public static class Options {
        private String A;
        private String B;
        private String C;
        private String D;
        private String answer;

        public Options() {}

        public static Options fromJson(String json) {
            try {
                Gson gson = new Gson();
                return gson.fromJson(json, Options.class);
            } catch (Exception e) {
                e.printStackTrace();
                return new Options();
            }
        }

        public String[] getOptionList() {
            List<String> options = new ArrayList<>();
            if (A != null) options.add(A);
            if (B != null) options.add(B);
            if (C != null) options.add(C);
            if (D != null) options.add(D);
            return options.toArray(new String[0]);
        }

        public String getAnswer() {
            return answer;
        }

        public String getOptionText(char option) {
            switch (option) {
                case 'A': return A;
                case 'B': return B;
                case 'C': return C;
                case 'D': return D;
                default: return null;
            }
        }

        public boolean isCorrectAnswer(char selectedOption) {
            return answer != null && answer.length() > 0 &&
                    answer.charAt(0) == selectedOption;
        }

        // 检查多选题答案是否正确
        public boolean isMultipleCorrectAnswer(String selectedAnswer) {
            if (answer == null || selectedAnswer == null) {
                return false;
            }

            // 将答案排序后比较
            String sortedSelected = sortString(selectedAnswer);
            String sortedCorrect = sortString(answer);

            return sortedSelected.equals(sortedCorrect);
        }

        private String sortString(String str) {
            if (str == null || str.isEmpty()) {
                return "";
            }
            char[] chars = str.toCharArray();
            java.util.Arrays.sort(chars);
            return new String(chars);
        }

        // 获取多选题正确答案列表
        public List<Character> getCorrectAnswerList() {
            List<Character> correctList = new ArrayList<>();
            if (answer != null && !answer.isEmpty()) {
                for (char c : answer.toCharArray()) {
                    correctList.add(c);
                }
            }
            return correctList;
        }
    }
}