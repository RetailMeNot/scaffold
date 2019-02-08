package com.retailmenot.scaffold.webelement;

import com.retailmenot.scaffold.BaseUnitTest;
import com.retailmenot.scaffold.webelements.ImageWebElement;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ImageWebElementTests extends BaseUnitTest {

    @Test
    public void testGetImageSource() {
        var srcTxt = "http://retailmenot.com";

        mockElement1.setAttribute("src", srcTxt);
        var image = new ImageWebElement(mockElement1);
        assertEquals(srcTxt, image.getImageSource() );
    }
}
