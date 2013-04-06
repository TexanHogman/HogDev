package com.hogdev.widgets;

import java.beans.BeanDescriptor;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;

public class EMailerBeanInfo extends SimpleBeanInfo
{
    private final static Class beanClass = EMailer.class;

    public BeanDescriptor getBeanDescriptor()
    {
        BeanDescriptor bd = new BeanDescriptor(beanClass);
        bd.setDisplayName("EMailer Bean");
        return bd;
    }

    public PropertyDescriptor[] getPropertyDescriptors()
    {
        try
        {
            PropertyDescriptor hostPD = new PropertyDescriptor("mailServer", beanClass);
            PropertyDescriptor toPD = new PropertyDescriptor("to", beanClass);
            PropertyDescriptor ccPD = new PropertyDescriptor("cc", beanClass);
            PropertyDescriptor fromPD = new PropertyDescriptor("from", beanClass);
            PropertyDescriptor subjectPD = new PropertyDescriptor("subject", beanClass);
            PropertyDescriptor messagePD = new PropertyDescriptor("message", beanClass);

            PropertyDescriptor rv[] = {hostPD, toPD, ccPD, fromPD, subjectPD, messagePD};
            toPD.setPropertyEditorClass(EMailerToPropertyEditor.class);
            ccPD.setPropertyEditorClass(EMailerToPropertyEditor.class);
            return rv;
        }
        catch (IntrospectionException e)
        {
            throw new Error(e.toString());
        }
    }
}