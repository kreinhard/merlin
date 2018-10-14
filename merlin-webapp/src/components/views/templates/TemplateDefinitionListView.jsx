import React from 'react'
import './TemplateListView.css';
import {CardGroup} from 'reactstrap';
import {PageHeader} from '../../general/BootstrapComponents';
import {getRestServiceUrl} from '../../../actions/global';
import ErrorAlert from '../../general/ErrorAlert';
import {IconRefresh} from "../../general/IconComponents";
import TemplateDefinitionCard from "./TemplateDefinitionCard";

class TemplateDefinitionListView extends React.Component {
    path = getRestServiceUrl('templates');
    state = {
        isFetching: false
    };

    componentDidMount = () => {
        this.fetchTemplateDefinitions();
    };

    fetchTemplateDefinitions = () => {
        this.setState({
            isFetching: true,
            failed: false,
            definitions: undefined
        });
        fetch(`${this.path}/definition-list`, {
            method: 'GET',
            headers: {
                'Accept': 'application/json'
            }
        })
            .then(response => response.json())
            .then(json => {
                this.setState({
                    isFetching: false,
                    definitions: json
                });
            })
            .catch(() => this.setState({isFetching: false, failed: true}));
    };

    render = () => {
        let content = undefined;

        if (this.state.isFetching) {

            content = <i>Loading...</i>;

        } else if (this.state.failed) {

            content = <ErrorAlert
                title={'Cannot load Template Definitions'}
                description={'Something went wrong during contacting the rest api.'}
                action={{
                    handleClick: this.fetchTemplateDefinitions,
                    title: 'Try again'
                }}
            />;

        } else if (this.state.definitions) {
            content = <div>
                <div
                    className={'template-list-refresh'}
                    onClick={this.fetchTemplateDefinitions}
                >
                    <IconRefresh/>
                </div>
                <CardGroup>
                    {this.state.definitions.map(definition => {
                        return <TemplateDefinitionCard
                            key={definition.fileDescriptor.primaryKey}
                            definition={definition}
                        />;
                    })}
                </CardGroup>
            </div>;

        }

        return <div>
            <PageHeader>
                Template definitions
            </PageHeader>
            {content}
        </div>;
    };

    constructor(props) {
        super(props);

        this.fetchTemplateDefinitions = this.fetchTemplateDefinitions.bind(this);
    }
}

export default TemplateDefinitionListView;
