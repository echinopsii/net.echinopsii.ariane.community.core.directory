<plan name="net.echinopsii.ariane.community.core.directory" version="{{version}}" scoped="false" atomic="false"
                xmlns="http://www.eclipse.org/virgo/schema/plan"
                xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                xsi:schemaLocation="
                        http://www.eclipse.org/virgo/schema/plan
                        http://www.eclipse.org/virgo/schema/plan/eclipse-virgo-plan.xsd">
        <artifact type="configuration" name="net.echinopsii.ariane.community.core.DirectoryJPAProviderManagedService"/>
    {% for s in submodules -%}
    {%- if module.type == 'none' %}
        <artifact type="bundle" name="net.echinopsii.ariane.community.{{module.name}}.{{s.name}}" version="[{{vmin}},{{vmax}})"/>
    {%- else -%}
        <artifact type="bundle" name="net.echinopsii.ariane.community.core.{{module.name}}.{{s.name}}" version="[{{vmin}},{{vmax}})"/>
    {% endif -%}
    {% endfor -%}
</plan>
