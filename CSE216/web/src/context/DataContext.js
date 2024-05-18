import { createContext, useState, useEffect } from 'react';
import useAxiosFetch from '../hooks/useAxiosFetch';

const DataContext = createContext({});

const backendURL = "http://127.0.0.1:4567";

export const DataProvider = ({ children }) => {
    const [posts, setPosts] = useState([]);

    const { data, fetchError, isLoading } = useAxiosFetch(`${backendURL}/posts`);

    useEffect(() => {
        setPosts(data);
    }, [data]);


    return (
        <DataContext.Provider value={{ fetchError, isLoading, posts, setPosts }}> {children} </DataContext.Provider>
    )
}

export default DataContext;