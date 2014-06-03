
package org.xbib.elasticsearch.plugin.simple;

import org.elasticsearch.action.ActionModule;
import org.elasticsearch.plugins.AbstractPlugin;
import org.xbib.elasticsearch.action.simple.SimpleAction;
import org.xbib.elasticsearch.action.simple.TransportSimpleAction;

public class SimplePlugin extends AbstractPlugin {

    @Override
    public String name() {
        return "simple" + "-"
                + Build.getInstance().getVersion() + "-"
                + Build.getInstance().getShortHash();
    }

    @Override
    public String description() {
        return "Simple plugin";
    }

    public void onModule(ActionModule module) {
        module.registerAction(SimpleAction.INSTANCE, TransportSimpleAction.class);
    }
}
