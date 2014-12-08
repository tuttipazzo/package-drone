package de.dentrassi.osgi.web.form.tags;

import javax.servlet.jsp.JspException;

public class Option extends OptionTagSupport
{
    private static final long serialVersionUID = 1L;

    private String value;

    private String label;

    @Override
    public int doStartTag () throws JspException
    {
        final WriterHelper writer = new WriterHelper ( this.pageContext );

        writer.write ( "<option" );
        writer.writeAttribute ( "value", this.value );
        writer.writeFlagAttribute ( "selected", isSelected ( this.value ) );
        writer.write ( " >" );

        writer.writeEscaped ( this.label != null ? this.label : this.value );

        writer.write ( "</option>" );

        return SKIP_BODY;
    }

    public void setValue ( final String value )
    {
        this.value = value;
    }

    public void setLabel ( final String label )
    {
        this.label = label;
    }

}