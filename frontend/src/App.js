import './App.css';
import {useEffect, useState} from "react";

const BASE_URL = 'http://localhost:8080/';

function sendRequest(url, body, callback, method) {
    fetch(BASE_URL + url, {
            method: method ?? 'POST',
            credentials: 'include',
            headers: typeof body === 'string' ? {'Content-Type': 'application/json'} : {},
            body
        }
    )
        .then((resp) => resp.json())
        .then(callback)
}

function App() {
    const [images, setImages] = useState([]);
    const [name, setName] = useState('');
    const [password, setPassword] = useState('');
    const [loginOrRegisterResult, setLoginOrRegisterResult] = useState('');
    const [uploadResult, setUploadResult] = useState('');
    const [uploadIsPrivate, setUploadIsPrivate] = useState(false);
    const [uploadDescription, setUploadDescription] = useState('');
    const [uploaderToSearch, setUploaderToSearch] = useState('');
    const [uploaderToSearchResult, setUploaderToSearchResult] = useState([]);

    const login = () => {
        sendRequest('user/login', JSON.stringify({
            name,
            password
        }), (res) => setLoginOrRegisterResult(JSON.stringify(res)))
    };

    const register = () => {
        sendRequest('user/register', JSON.stringify({
            name,
            password
        }), (res) => setLoginOrRegisterResult(JSON.stringify(res)))
    };

    const upload = (e) => {
        const formData = new FormData();
        formData.append('description', uploadDescription);
        formData.append('image', e.target.files[0]);
        formData.append('permission', uploadIsPrivate ? 'HIDDEN' : 'PUBLIC');
        sendRequest('images/upload', formData, (res) => setUploadResult(JSON.stringify(res)))
    };

    const tryDelete = (imageId) => {
        sendRequest('images/' + imageId, null, (res) => window.location.reload(), 'DELETE')
    };

    useEffect(() => {// fetch public images
        fetch(BASE_URL + "images/list").then((resp) => resp.json())
            .then((imgs) => setImages(imgs['images']))
    }, []);

    useEffect(() => {
        sendRequest('images/by/' + uploaderToSearch, null, (r) => setUploaderToSearchResult(r['images']), 'GET');
    }, [uploaderToSearch]);

    return (
        <div>
            <header>
                <h1>Gallery<span style={{color: '#6495ED'}}>Neo</span></h1>
                <p>
                    This is a simple demonstration of the basic functions of the image repository. It is
                    <span style={{fontWeight: 'bold'}}> not</span> written with robustness and user experience in
                    mind. It's quick and dirty. It assumes that a backend server is running at <i><a
                    href="http://localhost:8080">localhost:8080</a></i>
                </p>
            </header>
            <h1>User System</h1>
            Name: <input onChange={(e) => setName(e.target.value)}/>
            Password: <input type='password' onChange={(e) => setPassword(e.target.value)}/>
            <button onClick={login}>Login</button>
            <button onClick={register}>Register</button>
            <p>Result: <code>{loginOrRegisterResult}</code></p>

            <h1>Upload</h1>

            <p>Is it private: <input type="checkbox" onChange={(e) => setUploadIsPrivate(e.target.checked)}/></p>
            <p>(Optional) Description: <input onChange={(e) => setUploadDescription(e.target.value)}/></p>
            <input type="file" onChange={upload}/>
            <p>Result: <code>{uploadResult}</code></p>
            <p>You need to refresh the page to see your newly uploaded image</p>

            <h1>Public Gallery</h1>
            <p>Shows public images uploaded by you or other users</p>
            <div className='gallery'>
                {images.length === 0 ? <p>No image is uploaded yet.</p> : images.map((img) =>
                    <div className='image'>
                        <img src={BASE_URL + 'images/' + img['id']} alt={img['description']}/>
                        <div className='info'>
                            <p>Description: {img['description']}</p>
                            <p>Uploaded by: {img['uploaderName']}</p>
                            <p>Uploaded at: {new Date(img['timeUploadedMillis']).toLocaleString()}</p>
                            <a href={BASE_URL + 'images/' + img['id']}>Open</a>
                            <a href={BASE_URL + 'images/' + img['storeId']}>SHA 256 Direct link</a>
                            <button onClick={() => tryDelete(img['id'])}>Try to delete</button>
                        </div>
                    </div>
                )}
            </div>

            <h1>Search by Uploader</h1>
            If you are searching for your own uploads, it will show you the private ones you uploaded.
            <input onChange={(e) => setUploaderToSearch(e.target.value)}/>
            <p>Result:</p>
            {uploaderToSearchResult.map((i) =>
                <div>{i['id']} <a href={BASE_URL + 'images/' + i['id']}>Open</a> {i['description']}</div>
            )}
        </div>
    );
}

export default App;
