package com.hogdev.widgets;

import java.beans.PropertyEditorSupport;

public class EMailerToPropertyEditor extends PropertyEditorSupport
{
    public String[] getTags()
    {
        String[] strArray = {"@.com"};
        return strArray;
    }
}
