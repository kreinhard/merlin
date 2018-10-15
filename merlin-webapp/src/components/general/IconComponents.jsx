import React from 'react';
import {
    faCheck,
    faInfoCircle,
    faPlus,
    faSync,
    faTrash,
    faTimes,
    faUpload
} from '@fortawesome/free-solid-svg-icons'
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";

function IconAdd() {
    return (
        <FontAwesomeIcon icon={faPlus}/>
    );
}

function IconCancel() {
    return (
        <FontAwesomeIcon icon={faTimes}/>
    );
}

function IconCheck() {
    return (
        <FontAwesomeIcon icon={faCheck}/>
    );
}

function IconInfo() {
    return (
        <FontAwesomeIcon icon={faInfoCircle}/>
    );
}

function IconRefresh() {
    return (
        <FontAwesomeIcon icon={faSync}/>
    );
}

function IconRemove() {
    return (
        <FontAwesomeIcon icon={faTrash}/>
    );
}

function IconUpload() {
    return (
        <FontAwesomeIcon icon={faUpload}/>
    );
}

export {
    IconAdd, IconCancel, IconCheck, IconInfo, IconRefresh, IconRemove, IconUpload
};