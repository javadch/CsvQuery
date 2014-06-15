/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 *//*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 *//*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 *//*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.vaiona.data;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author standard
 */
public class AttributeInfo extends FieldInfo {
    // Data type is the data type defined in the adpater usable in Java and mapp-able to the underlying data
    // Type ref is the type declared by the query
    public String dataType = "";
    public String forwardMap = "";
    public String forwardMapTranslated = "";
    public List<String> fields = new ArrayList<>();
}
