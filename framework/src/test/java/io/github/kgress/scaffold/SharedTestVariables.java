package io.github.kgress.scaffold;

/**
 * A simple class for storing test variables that can appear in test code
 */
public class SharedTestVariables {

    public static final String ACTIVE_CLASS_NAME = "cool_class     active";
    public static final String CLASS_NAME = "LEIA_WUZ_HERE";
    public static final String CSS_SELECTOR1 = ".element1";
    public static final String TAG_NAME_1 = "test element 1";
    public static final String LINK_HREF = "http://hanshotfirst.sw";
    public static final String LINK_TEXT = "Han Shot First, fight me";
    public static final String SEND_KEYS_TEXT = "doing the thing!";
    public static final String ELEMENT_VALUE = "this is a value";
    public static final String DROPDOWN_VALUE = "Corellia";
    public static final String CLASS_ATTRIBUTE = "class";
    public static final String HREF_ATTRIBUTE = "href";
    public static final String VALUE_ATTRIBUTE = "value";
    public static final String IMAGE_SOURCE_ATTRIBUTE = "src";
    public static final String IMAGE_SOURCE_VALUE = "This source brought to you by wookies and jawas";
    public static final String TEXT_1 = "element 1";
    public static final String TEXT_2 = "element 2";
    public static final String PARENT_ELEMENT_SCRIPT = "return arguments[0].parentNode;";
    public static final String SCROLL_INTO_VIEW_SCRIPT =
        "arguments[0].scrollIntoView({block: 'center', inline: 'nearest'});";

    public static final String MOCK_PARENT_ELEMENT_SELECTOR = "#parent";
    public static final String MOCK_CHILD_ELEMENT_SELECTOR = "#child";
    public static final String EXPECTED_COMBINED_SELECTOR = String
            .format("%s %s", MOCK_PARENT_ELEMENT_SELECTOR, CSS_SELECTOR1);
}
