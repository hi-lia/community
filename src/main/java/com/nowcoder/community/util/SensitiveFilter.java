package com.nowcoder.community.util;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@Component
public class SensitiveFilter {

    private static final Logger logger = LoggerFactory.getLogger(SensitiveFilter.class);
    // 替换符
    private static final String REPLACEMENT = "***";
    // 根节点初始化
    private TrieNode rootNode = new TrieNode();

    // 初始化方法
    @PostConstruct
    public void init() {
        try(
                // 读敏感词文件 -> 字节流
                InputStream is = this.getClass().getClassLoader().getResourceAsStream("sensitive-words.txt");
                // 转成字符流 new InputStreamReader(is) -> 缓冲流
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                ) {
                String keyword;
                while ((keyword = reader.readLine()) != null) {
                    // 添加到前缀树
                    this.addKeyword(keyword);
                }
        } catch(IOException e) {
            logger.error("加载敏感词文件失败：" + e.getMessage());
        }

    }
    /**
     * 将一个敏感词添加到前缀树
     */
    private void addKeyword(String keyword) {
        TrieNode tempNode = rootNode;
        for (int i = 0; i < keyword.length(); i++) {
            char c = keyword.charAt(i);
            TrieNode subNode = tempNode.getSubNode(c);

            if(subNode == null) {
                // 初始化子节点
                subNode = new TrieNode();
                tempNode.addSubNode(c, subNode);
            }

            // 指向子节点，进入下一轮循环
            tempNode = subNode;

            // 设置结束标识
            if (i == keyword.length() - 1) {
                tempNode.setKeywordEnd(true);
            }
        }
    }

    /**
     * 过滤敏感词
     * @param text 待过滤的文本
     * @return 过滤后的文本
     */
    public String filter(String text) {
        // 文本为空
        if (StringUtils.isBlank(text)) {
            return null;
        }

        // 指针1
        TrieNode tempNode = rootNode;
        // 指针2
        int begin = 0;
        // 指针3
        int position = 0;
        // 结果
        StringBuilder sb = new StringBuilder();


        while (begin < text.length()) {
            if (position < text.length()) {
                char c = text.charAt(position);

                // 跳过符号 比如 "🌟开🌟票🌟“ 写一个函数isSymbol判断是否为符号
                if (isSymbol(c)) {
                    // 若指针1处于根节点，将此符号计入结果，让指针2向下走一步
                    if(tempNode == rootNode) {
                        begin++;
                        sb.append(c);
                    }
                    // 无论符号在开头或中间，指针3都向下走一步
                    position++;
                    continue;
                }

                // 检查下级结点
                tempNode = tempNode.getSubNode(c);
                // 不是敏感词(以begin开头的)
                if (tempNode == null) {
                    sb.append(text.charAt(begin));
                    position = ++begin;
                    tempNode = rootNode;
                } // 找到敏感词 begin ~ position
                else if (tempNode.isKeywordEnd) {
                    sb.append(REPLACEMENT);
                    begin = ++position;
                    tempNode = rootNode;
                } // 检查下一个字符
                else {
                    position++;
                }

            }  // position 遍历越界仍为匹配到敏感词
            // begin+1, position回到begin所在位置，指针1指向根节点
            else {
                sb.append(text.charAt(begin));
                position = ++begin;
                tempNode = rootNode;
            }


        }
        return sb.toString();
    }

    private boolean isSymbol(Character c) {
        // c is not an ASCII alphanumeric character(i.e., 'a'-'z', 'A'-'Z', '0'-'9') 0x2E80~0x9FFF 是东亚文字范围
        return !CharUtils.isAsciiAlphanumeric(c) && (c < 0x2E80 || c > 0x9FFF);
    }
    /**
     * 定义一个内部类 用于定义前缀树的结点
     */
    private class TrieNode {
        // 关键词结束标识
        private boolean isKeywordEnd = false;

        // 子节点 可能有多个 用map封装
        // key是下级字符，value是下级节点
        private Map<Character, TrieNode> subNodes = new HashMap<>();

        public boolean isKeywordEnd(){
            return isKeywordEnd;
        }

        public void setKeywordEnd(boolean keywordEnd){
            isKeywordEnd = keywordEnd;
        }

        public void addSubNode(Character c, TrieNode node) {
            subNodes.put(c, node);
        }

        public TrieNode getSubNode(Character c) {
            return subNodes.get(c);
        }


    }
}
