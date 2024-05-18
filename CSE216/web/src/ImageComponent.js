import React from 'react';

const ImageComponent = ({ base64 }) => {
  // Only proceed if base64 is not empty
  if (!base64) return null;

  // Assuming the image type is png, adjust as necessary
  const src = `data:image/png;base64,${base64}`;

  return <img src={src} alt="Attached" />;
};

export default ImageComponent;