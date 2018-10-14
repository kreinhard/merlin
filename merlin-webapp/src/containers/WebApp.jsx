import React from 'react';
import createBrowserHistory from 'history/createBrowserHistory';
import {Route, Router, Switch} from 'react-router';

import Menu from '../components/general/Menu';
import Start from '../components/views/Start';
import TemplateListView from '../components/views/templates/TemplateListView';
import TemplateDefinitionListView from '../components/views/templates/TemplateDefinitionListView';
import Config from '../components/views/config/Configuration';
import FileUploadView from '../components/views/FileUpload';
import RestServices from '../components/views/develop/RestServices';
import {isDevelopmentMode} from '../utilities/global';
import TemplateView from '../components/views/templates/TemplateView';
import LogView from '../components/views/logging/LogView';
import TemplateDefinition from '../components/views/templates/TemplateDefinition';

class WebApp extends React.Component {

    render() {
        const history = createBrowserHistory();
        let routes = [
            ['Start', '/', Start],
            ['Templates', '/templates', TemplateListView],
            ['Definitions', '/templateDefinitions', TemplateDefinitionListView],
            ['Log viewer', '/logViewer', LogView],
            ['Configuration', '/config', Config]
        ];
        if (isDevelopmentMode()) {
            routes.push(['File Upload', '/drop', FileUploadView]);
            routes.push(['Rest services', '/restServices', RestServices]);
        }

        return (
            <Router history={history}>
                <div>
                    <Menu routes={routes}/>
                    <div className={'container'}>
                        <Switch>
                            {
                                routes.map((route, index) => (
                                    <Route
                                        key={index}
                                        path={route[1]}
                                        component={route[2]}
                                        exact
                                    />
                                ))
                            }
                            <Route path={'/template/:primaryKey'} component={TemplateView} />
                            <Route path={'/templateDefinition/:primaryKey'} component={TemplateDefinition} />
                        </Switch>
                    </div>
                </div>
            </Router>
        );
    }
}

export default WebApp;
