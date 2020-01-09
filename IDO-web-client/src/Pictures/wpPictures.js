import React, { Component } from 'react';
import './wpPictures.css';
import Image1 from './Images/1.jpg'
import Image2 from './Images/2.jpg'
import Image3 from './Images/3.jpg'
import Image4 from './Images/4.jpg'
import Image5 from './Images/5.jpg'

class Pictures extends Component {

  constructor(props){
      super(props);
  }

  componentDidMount(){
    var rotator = document.getElementById('rotator');

    var delayInSeconds = 1;
    // list image names
    var images = [Image1,Image2, Image3, Image4, Image5];

    // don't change below this line
    var num = 0;
    var changeImage = function() {
        var len = images.length;
        rotator.src = images[num++];
        if (num == len) {
            num = 0;
        }
    };
    setInterval(changeImage, delayInSeconds * 5000);
  }

  render() {
    return (
      <div className="Pictures">
        <img id="rotator" class="ImgWedding" src={Image1} />
      </div>
    );
  }
}
export default Pictures;
