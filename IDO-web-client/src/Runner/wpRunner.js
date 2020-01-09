import React, { Component } from 'react';
import './wpRunner.css';
import Image1 from "./Images/logo.jpg"
import request from 'ajax-request'

class Runner extends Component {
    state = {
        msgArr: [],
        msgQR : []
    };

    constructor(props) {
        super(props);
        const baseUrl = "https://smartspace-2019b-sean.herokuapp.com:80";

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
                    msgQR: JSON.stringify(json.key)
                });

                setInterval(() => {
                    request({
                        url: baseUrl + "/smartspace/actions/story/2019b.sean.smartspace/lior.itzhakMANAGER@gmail.com/" + this.props.runnerData.smartspace + "/" + this.props.runnerData.id + "?page=0&size=4",
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
            <div className="Runner">
                <img className="logo" src={Image1}/>
                <div className="innerRunner">
                    {this.state.msgArr.map((msg) => {

                        return msg.imageUrl ?
                            <div className="PopMsgImg">
                                <img id="MsgImg" className="MsgImg" src={msg.imageUrl}/>
                                <p className="PopMsgText">{msg.blessing}</p>
                            </div>:
                            <div className="PopMsg">
                                <p className="PopMsgTextName" style={{color: "#0000009c"}}>{msg.username}</p>
                                <p className="PopMsgText">{msg.blessing}</p>
                            </div>
                    })}
                </div>
            </div>
        );
    }
}
export default Runner;
