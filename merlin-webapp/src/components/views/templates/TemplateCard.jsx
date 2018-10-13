import React from 'react';
import {Link} from 'react-router-dom';
import {Card, CardFooter, CardHeader} from 'reactstrap';

class TemplateCard extends React.Component {

    buildItem = (label, content) => {
        return <li className="list-group-item">{label}{content.map((line, index) => {
            let newLine = null;
            if (line[2] === 'description') {
                newLine = <br/>;
            }
            return <div className="card-list-entry" key={index}>{line[0]}:{newLine} <span className={`card-list-entry-value ${line[2]}`}>{line[1]}</span>
            </div>;
        })}</li>;
    }

    render = () => {
        const template = this.props.template;
        let templateId = template.id ? template.id : template.filename;
        let content = [['Filename', template.filename, 'filename']];
        let templateText = this.buildItem('Template', content);

        const definition = this.props.definition;
        let definitionText = null;
        if (!definition.autoGenerated) {
            content = [['refid', definition.id]];
            if (definition.fileDescriptor.filename) {
                content.push(['Filename', definition.fileDescriptor.filename, 'filename']);
            }
            if (definition.description) {
                content.push(['Description', definition.description, 'description']);
            }
            definitionText = this.buildItem('Definition', content);
        }

        return <div>
            <Link to={`/templates/${template.primaryKey}`} className={'card-link'}>
                <Card outline color="success" className={'template'} style={{backgroundColor: '#fff', width: '20em'}}>
                    <CardHeader>{templateId}</CardHeader>
                    <ul className="list-group list-group-flush">
                        {templateText}
                        {definitionText}
                    </ul>
                    <CardFooter>Click to run.</CardFooter>
                </Card>
            </Link>
        </div>
    };

    constructor(props) {
        super(props);

        this.buildItem = this.buildItem.bind(this);
    }
}

export default TemplateCard;