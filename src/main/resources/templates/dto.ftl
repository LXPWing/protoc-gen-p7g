package ${message.packagePath};

<#list message.importInfo as imports>
import ${imports};
</#list>


public class ${message.className} {

}