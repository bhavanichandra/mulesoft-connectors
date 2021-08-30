package com.themuler.internal;

import com.themuler.internal.connection.SymblAIConnectionProvider;
import com.themuler.internal.operations.CustomAction;
import org.mule.runtime.extension.api.annotation.Extension;
import org.mule.runtime.extension.api.annotation.Operations;
import org.mule.runtime.extension.api.annotation.connectivity.ConnectionProviders;
import org.mule.runtime.extension.api.annotation.dsl.xml.Xml;

@Xml(prefix = "symbl-ai")
@Extension(name = "Symblai")
@ConnectionProviders(SymblAIConnectionProvider.class)
@Operations({CustomAction.class})
public class SymblAIExtension {

}
