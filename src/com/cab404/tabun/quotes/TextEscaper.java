package com.cab404.tabun.quotes;

import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.style.*;
import com.cab404.moonlight.parser.HTMLTree;
import com.cab404.moonlight.parser.Tag;

/**
 * @author cab404
 */
public class TextEscaper {

    private static int indexOf(SpannableStringBuilder toProcess, int start, char ch) {
        if (start >= toProcess.length()) return -1;

        for (int i = start; i < toProcess.length(); i++)
            if (toProcess.charAt(i) == ch) return i;

        return -1;
    }

    /**
     * Searches for HTML tags and removes them.
     */
    private static SpannableStringBuilder removeAllTags(SpannableStringBuilder toProcess) {
        int s;

        while ((s = indexOf(toProcess, 0, '<')) != -1) {
            int f = indexOf(toProcess, s, '>');
            if (f == -1) break;
            toProcess.delete(s, f + 1);
        }

        return toProcess;
    }

    public static CharSequence simpleEscape(String text) {
        SpannableStringBuilder builder = new SpannableStringBuilder(text);
        HTMLTree tree = new HTMLTree(text);

        for (Tag tag : tree) {
            try {
                if (tag.isOpening())
                    switch (tag.name) {
                        case "strong":
                            builder.setSpan(
                                    new StyleSpan(Typeface.BOLD),
                                    tag.end,
                                    tree.get(tree.getClosingTag(tag)).start,
                                    0
                            );
                            break;
                        case "em":
                            builder.setSpan(
                                    new StyleSpan(Typeface.ITALIC),
                                    tag.end,
                                    tree.get(tree.getClosingTag(tag)).start,
                                    0
                            );
                            break;
                        case "s":
                            builder.setSpan(
                                    new StrikethroughSpan(),
                                    tag.end,
                                    tree.get(tree.getClosingTag(tag)).start,
                                    0
                            );
                            break;
                        case "u":
                            builder.setSpan(
                                    new UnderlineSpan(),
                                    tag.end,
                                    tree.get(tree.getClosingTag(tag)).start,
                                    0
                            );
                            break;
                        case "sup":
                            builder.setSpan(
                                    new RelativeSizeSpan(0.5f),
                                    tag.end,
                                    tree.get(tree.getClosingTag(tag)).start,
                                    0
                            );

                            break;
                        case "small":
                            builder.setSpan(
                                    new RelativeSizeSpan(0.5f),
                                    tag.end,
                                    tree.get(tree.getClosingTag(tag)).start,
                                    0
                            );
                            break;
                        case "sub":
                            builder.setSpan(
                                    new RelativeSizeSpan(0.5f),
                                    tag.end,
                                    tree.get(tree.getClosingTag(tag)).start,
                                    0
                            );
                            break;
                        case "a":
                            builder.setSpan(
                                    new URLSpan(tag.get("href")),
                                    tag.end,
                                    tree.get(tree.getClosingTag(tag)).start,
                                    0
                            );
                            break;
                        case "blockquote":
                            builder.setSpan(
                                    new QuoteSpan(Color.GRAY),
                                    tag.end,
                                    tree.get(tree.getClosingTag(tag)).start,
                                    0
                            );
                            break;
                        case "span":
                            if (tag.get("align").length() != 0) // Accessibility for API +3
                                switch (tag.get("align")) {
                                    case "right":
                                        builder.setSpan(
                                                new AlignmentSpan() {
                                                    @Override public Layout.Alignment getAlignment() {return Layout.Alignment.ALIGN_OPPOSITE;}
                                                },
                                                tag.end,
                                                tree.get(tree.getClosingTag(tag)).start,
                                                0
                                        );
                                        break;
                                    case "center":
                                        builder.setSpan(
                                                new AlignmentSpan() {
                                                    @Override public Layout.Alignment getAlignment() {return Layout.Alignment.ALIGN_CENTER;}
                                                },
                                                tag.end,
                                                tree.get(tree.getClosingTag(tag)).start,
                                                0
                                        );
                                        break;
                                    case "left":
                                        builder.setSpan(
                                                new AlignmentSpan() {
                                                    @Override public Layout.Alignment getAlignment() {return Layout.Alignment.ALIGN_NORMAL;}
                                                },
                                                tag.end,
                                                tree.get(tree.getClosingTag(tag)).start,
                                                0
                                        );
                                }

                    }
                else if (tag.isStandalone())
                    switch (tag.name) {
                        case "hr":
                            break;
                    }
            } catch (HTMLTree.TagNotFoundException e) {
            }
        }

        removeAllTags(builder);

        return builder;
    }

}
