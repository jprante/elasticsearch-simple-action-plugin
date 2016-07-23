package org.xbib.elasticsearch.plugin.simple;

import org.elasticsearch.action.ActionModule;
import org.elasticsearch.plugins.Plugin;
import org.xbib.elasticsearch.action.simple.SimpleAction;
import org.elasticsearch.action.search.TransportSimpleAction;

public class SimplePlugin extends Plugin {

    @Override
    public String name() {
        return "simple";
    }

    @Override
    public String description() {
        return "Simple plugin";
    }

    public void onModule(ActionModule module) {
        module.registerAction(SimpleAction.INSTANCE, TransportSimpleAction.class);
    }
}
