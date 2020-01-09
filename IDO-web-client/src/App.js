import React, { Component } from 'react';
import logo from './logo.svg';
import Runner from './Runner/wpRunner.js'
import Pictures from './Pictures/wpPictures.js'
import './App.css';
import request from 'ajax-request'

class App extends Component {
    state = {
        msgArr: [],
        msgQR : [],
        runnerData: {}
    };

    constructor(props) {
        super(props);
        const baseUrl = /*"http://localhost:8080"; //*/"https://smartspace-2019b-sean.herokuapp.com:80";

        request({
            url: baseUrl + "/smartspace/elements/2019b.sean.smartspace/lior.itzhakMANAGER@gmail.com",
            method: 'POST',
            data: {
                "location": null,
                "name": "test",
                "elementType": "Wedding",
                "expired": false,
                "moreAttributes": null
            }
        }, (err, res, body) => {
            if (body != null) {
                const json = JSON.parse(body);
                const smartspace = json.key.smartspace;
                const id = json.key.id;
                this.setState({
                    msgQR: JSON.stringify(json.key),
                    runnerData: { smartspace, id }
                });

                setInterval(() => {
                    request({
                        url: baseUrl + "/smartspace/actions/story/2019b.sean.smartspace/lior.itzhakMANAGER@gmail.com/" + smartspace + "/" + id + "?page=0&size=4",
                        method: 'GET'
                    }, (err, res, body) => {
                        if (body != null) {
                            this.setState({
                                msgArr: JSON.parse(body)
                            });
                        } else {
                            console.log(err)
                        }
                    });
                }, 10000);
            }
            else {
                console.log(err);
            }
        });
    }

    render() {
        return (
            <div className="App">
                <Runner runnerData={this.state.runnerData}/>
                <Pictures/>
                <img className="tryqr" src={"https://chart.googleapis.com/chart?cht=qr&chs=200x200&chl="+this.state.msgQR}/>
            </div>
        );
    }
}

export default App;
