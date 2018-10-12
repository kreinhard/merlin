import React from 'react';
import {Modal} from 'react-bootstrap';
import './Template.css';
import {getResponseHeaderFilename, getRestServiceUrl} from '../../../actions/global';
import downloadFile from '../../../utilities/download';
import DropArea from '../../general/droparea/Component';
import {FormButton, FormInput, FormLabelField, FormSelect} from '../../general/forms/FormComponents';

class Template extends React.Component {

    path = getRestServiceUrl('templates');
    filePath = getRestServiceUrl('files');
    state = {
        showRunModal: false,
        runConfiguration: this.props.variableDefinitions.reduce((accumulator, current) => ({
            ...accumulator,
            [current.name]: current.allowedValuesList && current.allowedValuesList.length !== 0 ?
                current.allowedValuesList[0] : ''
        }), {})
    };

    showRunModal = () => this.setState({
        showRunModal: true
    });

    closeRunModal = () => this.setState({
        showRunModal: false
    });

    handleRunConfigurationChange = (name) => (event) => this.setState({
        runConfiguration: {
            ...this.state.runConfiguration,
            [name]: event.target.value
        }
    });

    runSingleTemplate = (event) => {
        event.preventDefault();
        this.runTemplate(`${this.path}/run`, {
                'Content-Type': 'application/json'
            },
            JSON.stringify({
                templateDefinitionId: this.props.templateDefinitionId,
                templateCanonicalPath: this.props.canonicalPath,
                variables: this.state.runConfiguration
            })
        );
    }

    runSerialTemplate = (file) => {
        const formData = new FormData();
        formData.append('file', file);

        this.runTemplate(`${this.filePath}/upload`, undefined, formData);
    };

    runTemplate = (endpoint, headers, body) => {
        let filename;

        fetch(endpoint, {
            method: 'POST',
            headers,
            body: body
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error(response.statusText);
                }

                filename = getResponseHeaderFilename(response.headers.get('Content-Disposition'));
                return response.blob();
            })
            .then(blob => downloadFile(blob, filename))
            .catch(alert);
    };

    render = () => {
        let valid = true;

        return <div className={'template-container'}>
            <div
                className={'template'}
                onClick={this.showRunModal}
            >
                <span className={'template-name'}>{this.props.id}</span>
                <span className={'template-description'}>{this.props.description}</span>
                <span className={'hint'}>Click to run.</span>
            </div>

            <Modal
                bsSize="large"
                show={this.state.showRunModal}
                onHide={this.closeRunModal}
            >
                <Modal.Header>
                    <Modal.Title>
                        Run {this.props.id}
                    </Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <h4>Generation from File:</h4>
                    <DropArea
                        upload={this.runSerialTemplate}
                    />
                    <br/>
                    <h4>Single Generation:</h4>
                    <form onSubmit={this.runSingleTemplate}>
                        {this.props.variableDefinitions.map(item => {
                            let formControl;
                            const currentValue = this.state.runConfiguration[item.name];
                            let validationState;
                            const formControlProps = {
                                value: currentValue,
                                onChange: this.handleRunConfigurationChange(item.name)
                            };

                            if (item.allowedValuesList && item.allowedValuesList.length !== 0) {
                                formControl = <FormSelect
                                    {...formControlProps}
                                >
                                    {item.allowedValuesList.map(option => <option
                                        key={`template-run-variable-${item.refId}-select-${option}`}
                                        value={option}
                                    >
                                        {option}
                                    </option>)}
                                </FormSelect>
                            } else {

                                if ((item.required && currentValue.trim() === '') ||
                                    (item.type === 'INT' && isNaN(currentValue)) ||
                                    (item.minimumValue && item.minimumValue > Number(currentValue)) ||
                                    (item.maximumValue && item.maximumValue < Number(currentValue))) {
                                    validationState = 'error';
                                    valid = false;
                                }

                                if (item.minimumValue) {
                                    formControlProps.min = item.minimumValue;
                                }

                                if (item.maximumValue) {
                                    formControlProps.max = item.maximumValue;
                                }

                                if (item.type === 'INT') {
                                    item.type = 'number';
                                }

                                formControl = <FormInput
                                    {...formControlProps}
                                    type={item.type}
                                />;
                            }
                            return <FormLabelField
                                label={item.name}
                                labelLength={3}
                                fieldLength={9}
                                key={`template-run-variable-${item.refId}`}
                                validationState={validationState} hint={item.description}
                            >
                                {formControl}
                            </FormLabelField>;
                        })}
                        <FormButton
                            bsStyle={'success'}
                            type={'submit'}
                            disabled={!valid}
                        >
                            Run
                        </FormButton>
                    </form>
                </Modal.Body>
            </Modal>
        </div>;
    };

    constructor(props) {
        super(props);

        this.showRunModal = this.showRunModal.bind(this);
        this.closeRunModal = this.closeRunModal.bind(this);
        this.handleRunConfigurationChange = this.handleRunConfigurationChange.bind(this);
        this.runSerialTemplate = this.runSerialTemplate.bind(this);
    }
}

export default Template;