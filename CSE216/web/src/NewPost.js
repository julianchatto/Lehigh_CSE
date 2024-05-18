import { useState, useContext } from 'react';
import { useHistory } from 'react-router-dom';
import api from './api/posts';
import DataContext from './context/DataContext';

/**
 * Page to create a new post
 * @returns the NewPost component
 */
const NewPost = ({user}) => {
    const [postTitle, setPostTitle] = useState('');
    const [postBody, setPostBody] = useState('');
    const [base64, setBase64] = useState('');
    const [postLink, setPostLink] = useState(''); 
    const [postFile, setPostFile] = useState(null);
    const [isLoading, setIsLoading] = useState(false);
    const { posts, setPosts } = useContext(DataContext);
    const history = useHistory();

    const handleFileChange = (event) => {
        const file = event.target.files[0];
        if (file) {
            const reader = new FileReader();
            reader.onload = function (e) {
                setBase64(e.target.result);
            };
            reader.readAsDataURL(file);
        }
        setPostFile(file);
    };
    /**
     * Handles the submission of the form
     * @param {Event} e for the event
     */
    const handleSubmit = async (e) => {
        e.preventDefault();
        setIsLoading(true);
        try {
            let newPost;
            if (postFile ) {
                newPost = {Subject:postTitle, Message: postBody, webURL:postLink, file: base64, fileName: postFile.name};
            } else {
                newPost = {Subject:postTitle, Message: postBody, webURL:postLink};
            }
              // create a new post object
              console.log(newPost);
            const response = await api.post('/posts', newPost, {
                headers: {
                    'Cache-Control': "private, max-age=604800"
                },
            });
            
            const newPostUpdate = {ID: response.data.mData, Subject:postTitle, Message: postBody, VoteSum:0, UserName:user.Name, UserID:user.UserID, webURL:postLink, fileURL: response.data.mMessage};
            console.log(newPostUpdate);
            const allPosts = [newPostUpdate, ...posts]; // copy new post into the posts array
            setPosts(allPosts);

            // Reset the form and redirect to the home page
            setPostTitle('');
            setPostBody('');
            setPostLink('');
            setPostFile(null);
            setIsLoading(false);
            history.push('/');
        } catch (err) {
            console.log(`Error: ${err.message}`);
            setIsLoading(false);
        }
    }

    
    /**
     * Renders the NewPost component
     */
    return (
        <main className="NewPost">
            <h2>New Post</h2>
            <form className="newPostForm" onSubmit={handleSubmit}>
                <label htmlFor="postTitle">Title:</label>
                <input
                    id="postTitle"
                    type="text"
                    required
                    value={postTitle}
                    onChange={(e) => setPostTitle(e.target.value)}
                />
                <label htmlFor="postBody">Idea:</label>
                <textarea
                    id="postBody"
                    required
                    value={postBody}
                    onChange={(e) => setPostBody(e.target.value)}
                />
                <label htmlFor="postLink">Link:</label>
                <input
                    id="postTitle"
                    type="url"
                    value={postLink}
                    onChange={(e) => setPostLink(e.target.value)}
                />
                <label htmlFor="postFile">File:</label>
                <input
                    id="postFile"
                    type="file"
                    onChange={(e) => handleFileChange(e)}
                />
                {!isLoading ? <button type="submit">Submit</button> : <button type="submit">Loading...</button> }

            </form>
        </main>
    )
}

export default NewPost